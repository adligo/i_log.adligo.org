package org.adligo.i.log.client;

import org.adligo.i.util.client.Event;
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
	
	private static I_Map props = null;
	private static I_Listener deferredLog = null;
	private static I_NetLogDispatcher dispatcher = null;
	private static boolean isInit = false;
	

	public void onEvent(Event p) {
		if (p.threwException()) {
			p.getException().printStackTrace();
		} else {
			synchronized (LogPlatform.class) {
				props = (I_Map) p.getValue();
			}
			if (isInit) {
				LogFactory.instance.reset();
			}
		}
		if (deferredLog != null) {
			deferredLog.onEvent(p);
		}
	}

	/**
	 * 
	 * @param pLogConfignName
	 * @param p_deferredLog may be null for j2se (GWT needs this since adligo_log.properties
	 *    is loaded into the javascript runtime when it is recieved by the browser)
	 */
	public synchronized static final void init(String pLogConfignName, I_Listener p_deferredLog) {
		if (!isInit) {
			logConfigName = pLogConfignName;
			PropertyFactory.get(logConfigName, instance);
			LogFactory.init();
			deferredLog = p_deferredLog;
			isInit = true;
		}
	}

	public static final void init(String pLogConfignName) {
		init(pLogConfignName, null);
	}
	
	public static final void init(I_Listener deferredLog) {
		String name =getFileName();
		init(name, deferredLog);
	}
	public synchronized static final void init() {
		String name =getFileName();
		init(name, null);
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

	
	public static I_NetLogDispatcher getDispatcher() {
		return dispatcher;
	}

	public static void setDispatcher(I_NetLogDispatcher dispatcher) {
		LogPlatform.dispatcher = dispatcher;
	}
	
	/**
	 * Only call after init has been called!
	 * 
	 * this rereads the property file and sets all the log levels to match with it
	 */
	public synchronized static final void reloadProperties() {
		PropertyFactory.get(logConfigName, instance);
		//onEvent is called next through the callback
	}
}
