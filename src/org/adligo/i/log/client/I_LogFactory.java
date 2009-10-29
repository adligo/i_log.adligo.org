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
	 * most impls are not GWT in particular
	 * but log4j is
	 * @return
	 */
	public boolean isStaticInit();
	/**
	 * for static init logs (log4j and utilLog)
	 * set the log levels for the I_LogDelegate's that were obtained
	 * before the static init block was called
	 * 
	 * collection of ProxyLog
	 * @param i_proxyLogs
	 */
	public void setInitalLogLevels(I_Collection i_proxyLogs);
	
	/**
	 * for static init logs (log4j and utilLog)
	 * send the log messages that were logged before the
	 * log delegates were set up
	 * 
	 * collection of LogMessages
	 * 
	 * @param i_logMessages
	 */
	public void sendPreInitMessages(I_Collection i_logMessages);
	
	/**
	 * this is the adligo_log.properties file 
	 * or other log file name when its loaded by the 
	 * Log Platform
	 * @param props
	 * @param p
	 */
	public void setInitalLogLevels(I_Map props, I_LogFactory p); 
}
