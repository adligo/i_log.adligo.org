package org.adligo.i.log.client;

import org.adligo.i.log.client.models.I_LogMessage;
import org.adligo.i.util.client.I_Collection;
import org.adligo.i.util.client.I_Map;


/**
 * I suggest synchronizeing on these methods as well
 * 
 * @author scott
 *
 */
public interface I_LogFactory {
	/**
	 * since the apache commons logging stuff wouln't easily port to
	 * CLDC (cell phones), or GWT (Web Browsers JavaScript) this is my solution have a custom
	 * log factory api that can in turn get delegated to log4j or something
	 * else
	 * 
	 * @param c
	 * @return
	 */
	public I_LogDelegate getLog(Class clazz);
	public I_LogDelegate getLog(String name);
	
	/**
	 * re read config file on disk
	 */
	public void resetLogLevels();
	/**
	 * 
	 * @param i_proxyLogs
	 */
	public void setInitalLogLevels(I_Collection i_proxyLogs);
	
	public void sendPreInitMessages(I_Collection i_logMessages);
}
