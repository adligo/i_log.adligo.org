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
	private static final DefaultLogFactory DEFAULT_FACTORY = new DefaultLogFactory();
	private static I_LogFactory INSTANCE = DEFAULT_FACTORY;
	private String name;
	
	protected LogFactory() {
		name = "non-default";
	}
	
	protected LogFactory(String p) {
		name = p;
	}
	
	public String getName() {
		return name;
	}
	
	public static synchronized Log getLog(Class clazz) {
		return (Log) INSTANCE.getLog(clazz);
	}
	
	public static synchronized Log getLog(String clazz) {
		return (Log) INSTANCE.getLog(clazz);
	}
	
	protected static synchronized I_LogFactory getLogFactoryInstance() {
		return INSTANCE;
	}
	
	protected static boolean hasCustomFactory() {
		return !(INSTANCE == DEFAULT_FACTORY);
	}
	/**
	 * should only be used for static factory
	 * init from logFactory= in adligo_log.properties file
	 * 
	 * @param fac
	 */
	protected static synchronized void setLogFactoryInstance(I_LogFactory fac) {
		
		if (LogPlatform.isDebug()) {
			LogPlatform.log("LogFactory", " changing instance to " + fac);
		}
		if (fac == null) {
			// do nothing
			return;
		}
		if (INSTANCE == DEFAULT_FACTORY) {
			INSTANCE = fac;
			if (fac.isStaticInit()) {
				I_Collection preInitLogs = DEFAULT_FACTORY.memory.getDeferredLogsCollection();
				INSTANCE.setInitalLogLevels(preInitLogs);
				DEFAULT_FACTORY.memory.clearDeferredLogs();
				
				I_Collection deferredMessages = DeferredLog.deferredMessages;
				INSTANCE.sendPreInitMessages(deferredMessages);
				deferredMessages.clear();
			}
		} else {
			System.err.println("You can only set the LogFactory once " +
					"in a i_log runtime instance");
		}
		
	}
	
	protected static synchronized void resetLogLevels() {
		INSTANCE.resetLogLevels();
	}
	
	protected static synchronized void setInitalLogLevels(I_Map props, I_LogFactory p) {
		INSTANCE.setInitalLogLevels(props, p);
	}


	public static I_LogFactory getInstance() {
		return INSTANCE;
	}
	
	protected static void clearLogFactoryInstance() {
		INSTANCE= DEFAULT_FACTORY;
	}
}
