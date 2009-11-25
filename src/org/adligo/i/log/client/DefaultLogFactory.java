package org.adligo.i.log.client;

import org.adligo.i.log.client.models.LogMessage;
import org.adligo.i.util.client.ArrayCollection;
import org.adligo.i.util.client.I_Collection;
import org.adligo.i.util.client.I_Iterator;
import org.adligo.i.util.client.I_Map;
import org.adligo.i.util.client.MapFactory;

public class DefaultLogFactory implements I_LogFactory {
	/**
	 * collection of I_LogDelegates
	 */
	private ArrayCollection preInitLoggers = new ArrayCollection();
	protected static volatile I_Map loggers;
	protected static volatile boolean firstTime = true;
	
	
	public I_LogDelegate getLog(Class clazz) {
		if (clazz == null) {
			throw new NullPointerException(
					"LogFactory can't accept a null Class for \n" +
					" getLog(Class clazz)");
		}
		return getLogInternal(clazz.getName());
	}

	public I_LogDelegate getLog(String name) {
		return getLogInternal(name);
	}
	
	
	public synchronized I_LogDelegate getLogInternal(String clazz) {
		if (LogPlatform.isDebug()) {
			LogPlatform.log("LogFactory", " getLog(" + clazz + ")");
		}
		I_LogDelegate toRet;
		if (loggers == null) {
			toRet = new DeferredLog(clazz);
			I_LogDelegate current = (I_LogDelegate) preInitLoggers.get(toRet);
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
			toRet = (I_LogDelegate) loggers.get(clazz);
			if (toRet == null) {
				toRet = new SimpleLog(clazz, LogPlatform.getProps());
				loggers.put(clazz, toRet);
			}
		}
		return toRet;
	}
	
	public synchronized void resetLogLevels() {
		if (LogPlatform.isDebug()) {
			LogPlatform.log("LogFactory", " resetLogLevels()");
		}
		resetLevels(loggers, LogPlatform.getProps());
	}
	
	public synchronized void setInitalLogLevels(I_Map props, I_LogFactory p) {
		if (LogPlatform.isDebug()) {
			LogPlatform.log("LogFactory", " setInitalLogLevels " + p);
		}
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
			sendPreInitMessages(DeferredLog.deferredMessages);
		}
	}
	
	private static void resetLevels(I_Map loggers, I_Map props) {
		I_Iterator it = loggers.getIterator();
		if (LogPlatform.isDebug()) {
			LogPlatform.log("LogFactory","resetLevels there are " + loggers.size() + " loggers ");
		}
		
		while (it.hasNext()) {
			Object key = it.next();
			I_LogMutant log = (I_LogMutant) loggers.get(key);
			
			log.setLogLevel(props);
			if (LogPlatform.isDebug()) {
				LogPlatform.log("LogFactory","Log " + key + " is now at level " + 
						LogMessage.getLevelString(log.getLevel()));
			}
		}
	}

	public synchronized void setInitalLogLevels(I_Collection iProxyLogs) {
		throw new RuntimeException("not implemented in DefatultLogFactory. ");
	}

	public synchronized void sendPreInitMessages(I_Collection iLogMessages) {
		//originally came from DeferredLog.deferredMessages
		I_Iterator it = iLogMessages.getIterator();
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
						LogPlatform.log("LogFactory"," consuming log message \n" + message + 
								" el_log is at; \n" + LogMessage.getLevelString(el_log.getLevel()) +
								"\n");
					}
				}
			}
		}
		firstTime = false;
	}	
	
	public ArrayCollection getPreInitLogs() {
		return preInitLoggers;
	}

	public boolean isStaticInit() {
		return true;
	}
	
}
