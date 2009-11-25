package org.adligo.i.log.client;

import org.adligo.i.util.client.CollectionFactory;
import org.adligo.i.util.client.Event;
import org.adligo.i.util.client.I_Collection;
import org.adligo.i.util.client.I_Event;
import org.adligo.i.util.client.I_Iterator;
import org.adligo.i.util.client.I_Listener;
import org.adligo.i.util.client.I_Map;
import org.adligo.i.util.client.I_SystemOutput;
import org.adligo.i.util.client.I_ThreadPopulator;
import org.adligo.i.util.client.MapFactory;
import org.adligo.i.util.client.Platform;
import org.adligo.i.util.client.PropertyFactory;
import org.adligo.i.util.client.PropertyFileReadException;
import org.adligo.i.util.client.StringUtils;
import org.adligo.i.util.client.SystemOutput;
import org.adligo.i.util.client.ThreadPopulatorFactory;

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
	
	/**
	 * this is a key for adligo_log.properties
	 * that tells the LogPlatform what log factory to use
	 * 
	 * NOTE this was done after j2se, j2me and gwt log impls
	 *    and currently only supports log4j and java.util.logging
	 *    
	 *    obviously you can't use log4j on gwt or j2me,
	 *    but you can send those messages to a log servlet
	 *    and have them log there over http
	 *    
	 *    exc.  
	 *    read Samudra Gupta
	 *    Logging in Java
	 *    
	 *    written before i_log
	 *    
	 */
	public static final String LOG_FACTORY = "log_factory";
	public static I_Map //<String, I_LogFactory>
					logFactories = null;
	
	
	private static final LogPlatform instance = new LogPlatform();
	private static String logConfigName = null;
	private static boolean debug = false;
	private static boolean trace = false;
	
	private static I_Map props = null;
	private static I_LogDispatcher dispatcher = null;
	protected static boolean isInit = false;
	protected static boolean isInitLevelsSet = false;
	
	private static I_LogFactory customFactory = null;
	private static I_Formatter formatter;
	
	private static I_ThreadPopulator threadPopulator = null;
	private static I_SystemOutput out = SystemOutput.INSTANCE;
	
	public void onEvent(I_Event p) {
		if (debug) {
			System.out.println("entering onEvent in LogPlatform");
		}

		if (p.threwException()) {
			out.exception((PropertyFileReadException) p.getException());
			return;
		} 
		synchronized (LogPlatform.class) {
			Object value = p.getValue();
			
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
			
			String logFactory = (String) props.get(LOG_FACTORY);
			if ( !StringUtils.isEmpty(logFactory)) {
				I_LogFactory fac = (I_LogFactory) getLogFactories().get(logFactory);
				
				System.out.println("LogPlatform setting log_factory " + 
							logFactory + " instance " + fac);
				if (fac != null) {
					LogFactory.setLogFactoryInstance(fac);
				} else {
					throw new RuntimeException("log_factory is null, because your code" +
							" needs to call LogPlatform.addLogFactoryClass(String name, I_LogFactory p)" +
							" with a valid instance of your logFactory " + logFactory + "!");
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
			LogFactory.setInitalLogLevels(props, customFactory);
			String format = (String) props.get("format");
			if (!StringUtils.isEmpty(format)) {
				formatter.setFormatString(format);
			}
			isInitLevelsSet = true;
		} else {
			LogFactory.resetLogLevels();
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
			threadPopulator = ThreadPopulatorFactory.getThreadPopulator();
			//wait for file return event to set the LogFactory
			isInit = true;
		} else {
			throw new RuntimeException("LogPlatform has already been initialized.");
		}
	}


	/**
	 * this adds the ability to set a log_factory in adligo_log.properties
	 * 
	 * for instance if your using the i_log4log4j package
	 * you can  
	 *    LogPlatform.addLogFactoryClass(Log4jFactory.LOG_FACTORY_NAME,
	 *						Log4jFactory.INSTANCE);
	 *
	 * 
	 * @see adi_gwt_rpc_servlet 
	 *  AdiGwtServletInit class for a example
	 *  
	 * 
	 */
	public synchronized static final void addLogFactoryClass(String name, I_LogFactory p) {
		if (name != null) {
			getLogFactories().put(name, p);
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
		checkInit();
		logConfigName = pLogConfignName;
		init();
	}
	
	public static final void init(boolean p_debug) {
		checkInit();
		debug = p_debug;
		init(getFileName());
	}
	
	public static final void init() {
		checkInit();
		init(getFileName(), null);
	}
	
	private static void checkInit() {
		if (!Platform.isInit()) {
			throw new RuntimeException("Please initalize your platform BEFORE the LogPlatform," +
					" for instance J2SEPlatform.init(), GWTPlatform.init(), J2MEPlatform.init. ");
		}
	}
	private static String getFileName() {
		if (logConfigName != null) {
			if (Platform.getPlatform() == Platform.GWT) {
				return logConfigName;
			} else  {
				if (logConfigName.startsWith("/")) {
					return logConfigName;
				} else {
					return "/" + logConfigName;
				}
			}
		} else if (Platform.getPlatform() == Platform.GWT) {
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

	public static I_ThreadPopulator getThreadPopulator() {
		return threadPopulator;
	}

	public synchronized static void setThreadPopulator(I_ThreadPopulator threadPopulator) {
		LogPlatform.threadPopulator = threadPopulator;
	}
	
	private static I_Map getLogFactories() {
		if (logFactories == null) {
			logFactories = MapFactory.create();
		}
		return logFactories;
	}

	protected static I_SystemOutput getOut() {
		return out;
	}

	protected static void setOut(I_SystemOutput out) {
		LogPlatform.out = out;
	}
	
	public static boolean isInit() {
		return isInit;
	}
	
}
