package org.adligo.i.log.client;

import org.adligo.i.util.client.Event;
import org.adligo.i.util.client.I_Iterator;
import org.adligo.i.util.client.I_Listener;
import org.adligo.i.util.client.I_Map;
import org.adligo.i.util.client.Platform;
import org.adligo.i.util.client.PropertyFactory;

/**
 * this holds the properties which need to be used to init the 
 * Log Impl (SimpleLog in this case)
 * @author scott
 *
 */
public class LogPlatform implements I_Listener {
	/**
	 * this is a key in adligo_log.properties that
	 * is the url of a NetLog server
	 * which is part of the 
	 * 
	 * http://www.adligo.com/Component_License_Agreement_1.1.pdf
	 * 
	 * You can use it to serve log messages from 
	 * phones and GWT and J2SE if you want
	 * and log messages to a browser window
	 * 
	 */
	public static final String NET_LOG_URL = "net_log_url";
	
	private static final LogPlatform instance = new LogPlatform();
	private static String logConfigName = null;
	public static boolean log = false;
	
	private static I_Map props = null;
	private static I_LogDispatcher dispatcher = null;
	private static boolean isInit = false;
	private static boolean isInitLevelsSet = false;
	
	private static I_LogFactory customFactory = null;
	

	public void onEvent(Event p) {
		if (log) {
			System.out.println("entering onEvent in LogPlatform");
		}
		if (p.threwException()) {
			p.getException().printStackTrace();
		} else {
			synchronized (LogPlatform.class) {
				props = (I_Map) p.getValue();
			}
			if (!isInitLevelsSet) {
				if (log) {
					System.out.println("property file looked like...");
					I_Iterator it = props.getIterator();
					while (it.hasNext()) {
						Object key = it.next();
						Object value = props.get(key);
						System.out.println("" + key + "=" + value);
					}
				}
				LogFactory.instance.setInitalLogLevels(props, customFactory);
				isInitLevelsSet = true;
			} else {
				LogFactory.instance.resetLogLevels();
			}
		}
	}

	/**
	 * 
	 * @param pLogConfignName
	 * @param p the I_LogFactroy that you want to use to create Log instance
	 *    in this new version all Log instances created by your custom factory
	 *    will be called from ProxyLog->YourLog
	 *    if you need more than one log message reciever you should have your
	 *    custom I_LogFactory return a ProxyLog
	 *    
	 *    for instance if your code needed to call SimpleLog and NetLog 
	 *    (like most GWT will so it shows up in the eclipse console and over the web)
	 *    (I_Log's) ProxyLog-> (Your) ProxyLog
	 *    				-> SimpleLog
	 *    				-> NetLog
	 *    
	 */
	public synchronized static final void init(String pLogConfignName, I_LogFactory p) {
		if (!isInit) {
			logConfigName = pLogConfignName;
			PropertyFactory.get(logConfigName, instance);
			customFactory = p;
			isInit = true;
		}
	}

	public synchronized static final void resetLevels(String pLogConfignName) {
		logConfigName = pLogConfignName;
		PropertyFactory.get(pLogConfignName, instance);
	}
	/**
	 * 
	 * @param pLogConfignName
	 */
	public static final void init(String pLogConfignName) {
		init(getFileName());
	}
	
	public static final void init() {
		init(getFileName(), null);
	}
	
	private static String getFileName() {
		if (Platform.getPlatform() == Platform.GWT) {
			return "adligo_log.properties";
		} else {
			return "/adligo_log.properties";
		}
	}
	

	public synchronized static I_Map getProps() {
		return props;
	}

	
	public static I_LogDispatcher getDispatcher() {
		return dispatcher;
	}

	public static void setDispatcher(I_LogDispatcher dispatcher) {
		LogPlatform.dispatcher = dispatcher;
	}
	
	/**
	 * Only call after init has been called!
	 * 
	 * this rereads the property file and sets all the log levels to match with it
	 */
	public synchronized static final void reloadConfig() {
		PropertyFactory.get(logConfigName, instance);
		//onEvent is called next through the callback
	}
	
	public synchronized static final void loadConfig(String fileName) {
		logConfigName = fileName;
		PropertyFactory.get(logConfigName, instance);
		//onEvent is called next through the callback
	}
}
