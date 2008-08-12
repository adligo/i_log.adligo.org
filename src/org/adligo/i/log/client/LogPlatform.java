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
	
	private static I_Map props = null;
	private static I_Listener deferredLog = null;
	
	public void onEvent(Event p) {
		if (LogPlatform.isLogEnabled()) {
			LogPlatform.log("properties are loaded ");
		}
		
		if (p.threwException()) {
			p.getException().printStackTrace();
		} else {
			props = (I_Map) p.getValue();
		}
		if (deferredLog != null) {
			deferredLog.onEvent(p);
		}
	}

	public static final void init(String logConfignName, I_Listener p_deferredLog) {
		PropertyFactory.get(logConfignName, new LogPlatform());
		LogFactory.init();
		deferredLog = p_deferredLog;
	}
	
	public static final void init(I_Listener deferredLog) {
		String name =getFileName();
		init(name, deferredLog);
	}
	public static final void init() {
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
	
	protected static boolean isLogEnabled() {
		return false;
	}
	
	protected static void log(String p){
		System.out.println(p);
	}
}
