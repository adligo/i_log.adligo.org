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
	private static I_Map props = null;
	
	public void onEvent(Event p) {
		if (p.threwException()) {
			p.getException().printStackTrace();
		} else {
			props = (I_Map) p.getValue();
		}
		
	}

	public static final void init(String logConfignName) {
		PropertyFactory.get(logConfignName, new LogPlatform());
		LogFactory.init();
	}
	
	public static final void init() {
		if (Platform.getPlatform() == Platform.GWT) {
			init("adligo_log.properties");
		} else {
			init("/adligo_log.properties");
		}
	}

	public static I_Map getProps() {
		return props;
	}
	
	protected static boolean isLogEnabled() {
		return false;
	}
	
	protected static void log(String p){
		System.out.println(p);
	}
}
