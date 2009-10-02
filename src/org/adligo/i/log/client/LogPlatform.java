package org.adligo.i.log.client;

import java.lang.Throwable;
import java.util.HashSet;
import java.util.Set;

import org.adligo.i.util.client.CollectionFactory;
import org.adligo.i.util.client.Event;
import org.adligo.i.util.client.I_Collection;
import org.adligo.i.util.client.I_Event;
import org.adligo.i.util.client.I_Iterator;
import org.adligo.i.util.client.I_Listener;
import org.adligo.i.util.client.I_Map;
import org.adligo.i.util.client.Platform;
import org.adligo.i.util.client.PropertyFactory;
import org.adligo.i.util.client.StringUtils;

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
	private static boolean debug = false;
	private static boolean trace = false;
	
	private static I_Map props = null;
	private static I_LogDispatcher dispatcher = null;
	private static boolean isInit = false;
	private static boolean isInitLevelsSet = false;
	
	private static I_LogFactory customFactory = null;
	private static I_Formatter formatter;

	public void onEvent(I_Event p) {
		if (debug) {
			System.out.println("entering onEvent in LogPlatform");
		}
		if (p.threwException()) {
			p.getException().printStackTrace();
		} else {
			synchronized (LogPlatform.class) {
				props = (I_Map) p.getValue();
				I_Iterator it =  props.getIterator();
				//remove item with #
				I_Collection items = CollectionFactory.create();
				while (it.hasNext()) {
					items.add(it.next());
				}
				
				it = items.getIterator();
				while (it.hasNext()) {
					String key = (String) it.next();
					if (key.indexOf("#") != -1) {
						if (debug) {
							log("LogPlatform", " removing property " + key);
						}
						props.remove(key);
					}
				}
			}
			if (!isInitLevelsSet) {
				if (debug) {
					System.out.println("property file looked like...");
					I_Iterator it = props.getIterator();
					while (it.hasNext()) {
						Object key = it.next();
						Object value = props.get(key);
						System.out.println("" + key + "=" + value);
					}
				}
				LogFactory.instance.setInitalLogLevels(props, customFactory);
				String format = (String) props.get("format");
				if (!StringUtils.isEmpty(format)) {
					formatter.setFormatString(format);
				}
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
			formatter = new SimpleFormatter();
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
		logConfigName = pLogConfignName;
		init();
	}
	
	public static final void init(boolean p_debug) {
		debug = p_debug;
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

	public synchronized static boolean isDebug() {
		return debug;
	}

	public synchronized static void setDebug(boolean log) {
		LogPlatform.debug = log;
	}

	public synchronized static boolean isTrace() {
		return trace;
	}

	public synchronized static void setTrace(boolean trace) {
		LogPlatform.trace = trace;
	}
	
	public static void log(String clazz, String message) {
		System.out.println(clazz + " due to LogPlatform.isDebug() " + message);
	}
	
	public static void trace(String clazz, String message) {
		System.err.println(clazz + " due to LogPlatform.isTrace() " + message);
	}
	public static void trace(String clazz, Exception message) {
		System.err.println(clazz + " due to LogPlatform.trace ");
		message.printStackTrace();
	}
	
	public synchronized static I_Formatter getFormatter() {
		return formatter;
	}

	public synchronized static void setFormatter(I_Formatter formatter) {
		LogPlatform.formatter = formatter;
	}
}
