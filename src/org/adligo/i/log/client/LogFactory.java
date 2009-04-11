package org.adligo.i.log.client;

import org.adligo.i.util.client.ArrayCollection;
import org.adligo.i.util.client.I_Iterator;
import org.adligo.i.util.client.I_Map;
import org.adligo.i.util.client.MapFactory;

/**
 * a ultra simple log factory for CLDC 2.0 usage
 * in cell phones and GWT 
 * exc 
 * 
 * @author scott
 *
 */
public class LogFactory {
	protected static final LogFactory instance = new LogFactory();
	private ArrayCollection preInitLoggers = new ArrayCollection();
	volatile private I_Map loggers;
	volatile boolean firstTime = true;
	
	private LogFactory() {}
	
	public static Log getLog(Class clazz) {
		if (clazz == null) {
			throw new NullPointerException(
					"LogFactory can't accept a null Class for \n" +
					" getLog(Class clazz)");
		}
		return instance.getLogInternal(clazz.getName());
	}
	
	public static Log getLog(String clazz) {
		return instance.getLogInternal(clazz);
	}
	
	public synchronized Log getLogInternal(String clazz) {
		//System.out.println("getting log for " + clazz);
		
		Log toRet;
		if (loggers == null) {
			toRet = new DeferredLog(clazz);
			Log current = (Log) preInitLoggers.get(toRet);
			if (LogPlatform.log) {
				System.out.println("toRet is " + toRet +
						" current is " + current);
			}
			if (current != null) {
				toRet = current;
			} else {
				preInitLoggers.add(toRet);
			}
		} else {
			toRet = (Log) loggers.get(clazz);
			if (toRet == null) {
				toRet = new SimpleLog(clazz, LogPlatform.getProps());
				loggers.put(clazz, toRet);
			}
		}
		return toRet;
	}

	public synchronized void resetLogLevels(I_Map props, I_LogFactory p) {
		if (loggers == null) {
			firstTime = true;
			loggers = MapFactory.create();
			I_Iterator it = preInitLoggers.getIterator();
			while (it.hasNext()) {
				DeferredLog log = (DeferredLog) it.next();
				I_LogDelegate delegate = null;
				if (p != null) {
					delegate = p.getLog(log.getLogClass());
				} else {
					String clazz = log.getLogClass();
					delegate = new SimpleLog(clazz, props);
				}
				log.addDelegate(delegate);
				loggers.put(log.getLogClass(), log);
			}
		}
		resetLevels(loggers, LogPlatform.getProps());
		if (firstTime) {
			I_Iterator it = DeferredLog.deferredMessages.getIterator();
			if (LogPlatform.log) {
				System.out.println("there are " + DeferredLog.deferredMessages.size() + 
						" deferred log messages.");
			}
			while (it.hasNext()) {
				LogMessage message = (LogMessage) it.next();
				((DeferredLog) message.getLog()).consumeMessage(message);
				if (LogPlatform.log) {
					System.out.println("consuming log message " + message + 
							" with log " + message.getLog() + " at level " + message.getLog().getLevel());
				}
			}
			firstTime = false;
		}
	}
	
	private static void resetLevels(I_Map loggers, I_Map props) {
		I_Iterator it = loggers.getIterator();
		while (it.hasNext()) {
			Object key = it.next();
			I_LogMutant log = (I_LogMutant) loggers.get(key);
			
			log.setLogLevel(props);
		}
	}	
	
}
