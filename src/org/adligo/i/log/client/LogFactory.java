package org.adligo.i.log.client;

import org.adligo.i.util.client.I_Collection;
import org.adligo.i.util.client.I_Map;

/**
 * a ultra simple log factory for CLDC 2.0 usage
 * in cell phones and GWT 
 * exc 
 * 
 * @author scott
 *
 */
public class LogFactory  {
	public static final DefaultLogFactory DEFAULT_FACTORY = new DefaultLogFactory();
	private static I_LogFactory instance = DEFAULT_FACTORY;
	
	private LogFactory() {}
	
	
	public static synchronized Log getLog(Class clazz) {
		return (Log) instance.getLog(clazz);
	}
	
	public static synchronized Log getLog(String clazz) {
		return (Log) instance.getLog(clazz);
	}
	
	protected static synchronized I_LogFactory getLogFactoryInstance() {
		return instance;
	}
	
	protected static boolean hasCustomFactory() {
		return !(instance == DEFAULT_FACTORY);
	}
	protected static synchronized void setLogFactoryInstance(I_LogFactory fac) {
		
		if (LogPlatform.isDebug()) {
			LogPlatform.log("LogFactory", " changing instance to " + fac);
		}
		if (fac == null) {
			// do nothing
			return;
		}
		if (instance == DEFAULT_FACTORY) {
			instance = fac;
			
			if (fac.isStaticInit()) {
				I_Collection preInitLogs = DEFAULT_FACTORY.getPreInitLogs();
				instance.setInitalLogLevels(preInitLogs);
				preInitLogs.clear();
				
				I_Collection deferredMessages = DeferredLog.deferredMessages;
				instance.sendPreInitMessages(deferredMessages);
				deferredMessages.clear();
			}
		} else {
			System.err.println("You can only set the LogFactory once " +
					"in a i_log runtime instance");
		}
		
	}
	
	protected static synchronized void resetLogLevels() {
		instance.resetLogLevels();
	}
	
	protected static synchronized void setInitalLogLevels(I_Map props, I_LogFactory p) {
		instance.setInitalLogLevels(props, p);
	}
}
