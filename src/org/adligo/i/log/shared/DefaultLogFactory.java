package org.adligo.i.log.shared;

import org.adligo.i.log.shared.models.LogMessage;
import org.adligo.i.util.shared.AppenderFactory;
import org.adligo.i.util.shared.I_Collection;
import org.adligo.i.util.shared.I_ImmutableMap;
import org.adligo.i.util.shared.I_Iterator;

public class DefaultLogFactory implements I_LogFactory {
	/**
	 * collection of I_LogDelegates
	 */
	protected static final LogFactoryMemory memory = new LogFactoryMemory();
	
	
	public I_LogDelegate getLog(Class clazz) {
		if (clazz == null) {
			throw new NullPointerException(
					"LogFactory can't accept a null Class for " +
					AppenderFactory.lineSeperator() +
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
		if ( !memory.isInit()) {
			
			DeferredLog current = memory.getDeferredLog(clazz);
			if (LogPlatform.isDebug()) {
				LogPlatform.log("LogFactory"," current is " + current);
			}
			if (current != null) {
				toRet = current;
			} else {
				current = new DeferredLog(clazz);
				toRet = current;
				memory.addDeferredLog(current);
			}
		} else {
			toRet = memory.getLogger(clazz);
			if (toRet == null) {
				toRet = createPostInitLogger(clazz);
				memory.addLogger(clazz, toRet);
			}
		}
		return toRet;
	}
	
	/**
	 * this allows subclasses to override which 
	 * log is delivered (ie see ant_log.adligo.org project)
	 * 
	 * @param clazz
	 * @return
	 */
	protected I_LogDelegate createPostInitLogger(String clazz) {
		return new SimpleLog(clazz, LogPlatform.getProps());
	}
	
	public synchronized void resetLogLevels() {
		if (LogPlatform.isDebug()) {
			LogPlatform.log("LogFactory", " resetLogLevels()");
		}
		resetLevels(LogPlatform.getProps());
	}
	
	public synchronized void setInitalLogLevels(I_ImmutableMap props, I_LogFactory p) {
		if (LogPlatform.isDebug()) {
			LogPlatform.log("LogFactory", " setInitalLogLevels " + p);
		}
		if (!memory.isInit()) {
			memory.init();
			I_Iterator it = memory.getDeferredLogs();
			while (it.hasNext()) {
				Object objLog = it.next();
				try {
					DeferredLog log = (DeferredLog) objLog;
					I_LogDelegate delegate = null;
					if (p != null) {
						delegate = p.getLog(log.getLogName());
					} else {
						String clazz = log.getLogName();
						delegate = new SimpleLog(clazz, props);
					}
					log.addDelegate(delegate);
					memory.addLogger(log.getLogName(), log);
				} catch (ClassCastException x) {
					System.out.println("objLog is a " + objLog);
					x.printStackTrace();
				}
			}
		}
		resetLevels(LogPlatform.getProps());
		if (memory.isfirstCallToSetInitalLogLevels()) {
			sendPreInitMessages(DeferredLog.deferredMessages);
		}
		memory.firstCallToSetInitalLogLevelsFinished();
	}
	
	private static void resetLevels(I_ImmutableMap props) {
		I_Iterator it = memory.getLogs();
		
		while (it.hasNext()) {
			I_LogMutant log = (I_LogMutant) it.next();
			
			log.setLogLevel(props);
			if (LogPlatform.isDebug()) {
				LogPlatform.log("LogFactory","Log " + log + " is now at level " + 
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
				String logName = message.getName();
				DeferredLog el_log = (DeferredLog) memory.getDeferredLog(logName);
				if (el_log != null) {
					el_log.consumeMessage(message);
					if (LogPlatform.isDebug()) {
						LogPlatform.log("LogFactory"," consuming log message " + 
								AppenderFactory.lineSeperator() + message + 
								" el_log is at; " + AppenderFactory.lineSeperator() +
								LogMessage.getLevelString(el_log.getLevel()) +
								AppenderFactory.lineSeperator());
					}
				}
			}
		}
		iLogMessages.clear();
	}	


	public boolean isStaticInit() {
		return true;
	}

	public String getName() {
		return DefaultLogFactory.class.getName();
	}
	
}
