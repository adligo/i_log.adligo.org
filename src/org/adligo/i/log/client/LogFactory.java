package org.adligo.i.log.client;

import org.adligo.i.util.client.ArrayCollection;
import org.adligo.i.util.client.ClassUtils;
import org.adligo.i.util.client.CollectionFactory;
import org.adligo.i.util.client.Event;
import org.adligo.i.util.client.I_Collection;
import org.adligo.i.util.client.I_Iterator;
import org.adligo.i.util.client.I_Listener;
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
	private I_Collection preInitLoggers = new ArrayCollection();
	volatile private I_Map loggers;
	volatile boolean firstTime = true;
	
	private LogFactory() {}
	
	public static Log getLog(Class clazz) {
		return instance.getLogInternal(clazz);
	}
	
	public synchronized Log getLogInternal(Class clazz) {
		//System.out.println("getting log for " + clazz);
		
		Log toRet;
		if (loggers == null) {
			// its ok if a log get added twice here,
			// when initalization happens it will be normalized before
			// being put in the lookup
			toRet = new DeferredLog(clazz);
			preInitLoggers.add(toRet);
		} else {
			toRet = (Log) loggers.get(clazz);
			if (toRet == null) {
				String name = ClassUtils.getClassName(clazz);
				toRet = new SimpleLog(name, LogPlatform.getProps());
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
				Log delegate = null;
				if (p != null) {
					delegate = p.getLog(log.getLogClass());
				} else {
					Class clazz = log.getLogClass();
					delegate = new SimpleLog(clazz.getName(), props);
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
			LogMutant log = (LogMutant) loggers.get(key);
			
			log.setLogLevel(props);
		}
	}	
	
}
