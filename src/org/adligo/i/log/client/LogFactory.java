package org.adligo.i.log.client;

import org.adligo.i.log.client.models.LogMessage;
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
			if (LogPlatform.isDebug()) {
				LogPlatform.log("LogFactory","toRet is " + toRet +
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
	public synchronized void resetLogLevels() {
		resetLevels(loggers, LogPlatform.getProps());
	}
	
	public synchronized void setInitalLogLevels(I_Map props, I_LogFactory p) {
		if (loggers == null) {
			firstTime = true;
			loggers = MapFactory.create();
			I_Iterator it = preInitLoggers.getIterator();
			while (it.hasNext()) {
				DeferredLog log = (DeferredLog) it.next();
				I_LogDelegate delegate = null;
				if (p != null) {
					delegate = p.getLog(log.getLogName());
				} else {
					String clazz = log.getLogName();
					delegate = new SimpleLog(clazz, props);
				}
				log.addDelegate(delegate);
				loggers.put(log.getLogName(), log);
			}
		}
		resetLevels(loggers, LogPlatform.getProps());
		if (firstTime) {
			I_Iterator it = DeferredLog.deferredMessages.getIterator();
			if (LogPlatform.isDebug()) {
				LogPlatform.log("LogFactory","there are " + DeferredLog.deferredMessages.size() + 
						" deferred log messages.");
			}
			while (it.hasNext()) {
				LogMessage message = (LogMessage) it.next();
				if (message != null) {
					DeferredLog el_log = (DeferredLog) loggers.get(message.getName());
					if (el_log != null) {
						el_log.consumeMessage(message);
						if (LogPlatform.isDebug()) {
							LogPlatform.log("LogFactory"," consuming log message " + message + 
									" with log " + message.getName() + " at level " + message.getLevel() +
									" el_log is at " + el_log.getLevel());
						}
					}
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
			if (LogPlatform.isDebug()) {
				LogPlatform.log("LogFactory","Log " + key + " is now at level " + log.getLevel());
			}
		}
	}	
	
}
