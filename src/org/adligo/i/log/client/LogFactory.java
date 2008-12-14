package org.adligo.i.log.client;


import org.adligo.i.util.client.Event;
import org.adligo.i.util.client.I_Listener;
import org.adligo.i.util.client.I_Map;
import org.adligo.i.util.client.Platform;
import org.adligo.i.util.client.PropertyFactory;

public class LogFactory {
	public static final String LOG_FACTORY_IMPL = "log_factory_class";
	private static I_LogFactoryContainer container = new AdligoLogFactory();
	protected static  I_LogFactory instance;
	
	/**
	 * NOTE in j2ME and J2SE code this should assign to a 
	 * static variable
	 * ie.
	 * private static final Log log = LogFactory.getLog(Class.class);
	 * 
	 * in GWT code this should assign to a member variable!
	 * ie.
	 * private final Log log = LogFactory.getLog(Class.class);
	 * 
	 * This is because the adligo_log.property file
	 * is loaded after runtime in a Async call, not
	 * as part of the classpath
	 * 
	 * Also it should have it's reference removed if you 
	 * need to GarbageCollect the memory (RAM) consumed by the 
	 * object
	 * 
	 * @param clazz
	 * @return
	 */
	public static Log getLog(Class clazz) {
		if (instance == null) {
			throw new NullPointerException(
				"The org.adligo.i.log.LogPlatform has not been initalized " +
				"please call init on it first!"	
			);
			
		}
		return instance.getLog(clazz);
	}
	
	
	
	/**
	 * @return
	 * GWT and J2ME bootstrapping and having optional instances
	 * became to big of a pain
	 * 
	 * if you want to take a crack at it go ahead
	 * 
	 */
	protected static synchronized void init() {
		instance = container.getLogFactory();
	}

	/**
	 * this could be use in GWT initaliztion
	 * to log to Http -> Hibernate -> Oracle for 
	 * example
	 * 
	 * @param container
	 */
	public synchronized static void setContainer(I_LogFactoryContainer container) {
		LogFactory.container = container;
	}
	


}
