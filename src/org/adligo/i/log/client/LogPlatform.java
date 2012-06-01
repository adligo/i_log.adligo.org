package org.adligo.i.log.client;

import org.adligo.i.util.client.CollectionFactory;
import org.adligo.i.util.client.Event;
import org.adligo.i.util.client.I_Collection;
import org.adligo.i.util.client.I_Event;
import org.adligo.i.util.client.I_ImmutableMap;
import org.adligo.i.util.client.I_Iterator;
import org.adligo.i.util.client.I_Listener;
import org.adligo.i.util.client.I_Map;
import org.adligo.i.util.client.I_SystemOutput;
import org.adligo.i.util.client.I_ThreadPopulator;
import org.adligo.i.util.client.InstanceForName;
import org.adligo.i.util.client.MapFactory;
import org.adligo.i.util.client.Platform;
import org.adligo.i.util.client.PropertyFactory;
import org.adligo.i.util.client.PropertyFileReadException;
import org.adligo.i.util.client.StringUtils;
import org.adligo.i.util.client.SystemOutput;
import org.adligo.i.util.client.ThreadPopulatorFactory;
import org.adligo.i.util.client.ThrowableHelperFactory;

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
	
	
	protected static final LogPlatform instance = new LogPlatform();
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

		Object value = p.getValue();
		props = (I_Map) p.getValue();
		if (p.threwException()) {
			Throwable ex = p.getException();
			try {
				PropertyFileReadException pfre = (PropertyFileReadException) ex;
				out.err("error reading file '" + pfre.getFileName() + "' system file '" +
						pfre.getAttemptedSystemFileName() + "' using defaults;");
				props = getDefaults();
				out.err("using defaults " + props);
			} catch (ClassCastException x) {
				out.exception(p.getException());
			}
		} 
		synchronized (LogPlatform.class) {
			I_Iterator it =  props.getKeysIterator();
			
			removeCommentLines(it);
			
			String logFactory = (String) props.get(LOG_FACTORY);
			if ( !StringUtils.isEmpty(logFactory)) {
				
					
				switch (Platform.getPlatform()) {
					case Platform.GWT:
						out.err("LogPlatform can't set dynamic log_factory from adligo_log.properties on GWT," +
								" you must use the org.adligo.gwt.util.client.GwtLogFactory class instead. ");
						break;
					case Platform.JME:
						out.err("LogPlatform can't set dynamic log_factory from adligo_log.properties on J2ME.");
						break;
					case Platform.JSE:
							I_LogFactory fac = (I_LogFactory) InstanceForName.create(logFactory);
							out.err("LogPlatform setting log_factory " + 
									logFactory + " instance " + fac);
							if (fac != null) {
								LogFactory.setLogFactoryInstance(fac);
							} else {
								Exception ex =  new Exception("log_factory is null, because your code" +
										" needs to call LogPlatform.init(String name, I_LogFactory p)" +
										" with a valid instance of your logFactory " + logFactory + "!");
								ThrowableHelperFactory.fillInStackTrace(ex);
								out.exception(ex);
							}
							break;
				}
			}
		}
		if (!isInitLevelsSet) {
			if (debug) {
				System.out.println("property file looked like...");
				I_Iterator it = props.getKeysIterator();
			}
			String format = (String) props.get("format");
			if (!StringUtils.isEmpty(format)) {
				formatter.setFormatString(format);
			}
			LogFactory.setInitalLogLevels(props, customFactory);
			isInitLevelsSet = true;
		} else {
			LogFactory.resetLogLevels();
		}
	}

	private void removeCommentLines(I_Iterator it) {
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

	/**
	 * 
	 * @param pFileName a relative url (on GWT)
	 *    or a file from the filesystem or classpath
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
	public synchronized static final void init(String pFileName, I_LogFactory p) {
		if (!isInit) {
			formatter = new SimpleFormatter();
			logConfigName = pFileName;
			customFactory = p;
			PropertyFactory.get(logConfigName, instance);
			threadPopulator = ThreadPopulatorFactory.getThreadPopulator();
			//wait for file return event to set the LogFactory
			isInit = true;
		} else {
			throw new RuntimeException("LogPlatform has already been initialized.");
		}
	}

	/**
	 * 
	 * @param pFileName
	 *    a relative url (on GWT)
	 *    or a file from the filesystem or classpath
	 */
	public synchronized static final void resetLevels(String pFileName) {
		out.out("Reading in log file " + pFileName);
		logConfigName = pFileName;
		PropertyFactory.get(pFileName, instance);
	}
	/**
	 * 
	 * @param pFileName
	 *    a relative url (on GWT)
	 *    or a file from the filesystem or classpath
	 */
	public static final void init(String pFileName) {
		checkInit();
		logConfigName = pFileName;
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
					" for instance JSEPlatform.init(), GWTPlatform.init(), J2MEPlatform.init. ");
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
	
	public synchronized static I_ImmutableMap getProps() {
		I_ImmutableMap toRet = MapFactory.create(props);
		return toRet;
	}

	public synchronized static I_LogDispatcher getDispatcher() {
		return dispatcher;
	}

	public synchronized static void setDispatcher(I_LogDispatcher dispatcher) {
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
	

	protected static I_SystemOutput getOut() {
		return out;
	}

	protected static void setOut(I_SystemOutput out) {
		LogPlatform.out = out;
	}
	
	public static boolean isInit() {
		return isInit;
	}
	
	public I_Map getDefaults() {
		I_Map map = MapFactory.create();
		map.put("defaultlog", "INFO");
		map.put("gwt_loggers", "simple");
		return map;
	}
}
