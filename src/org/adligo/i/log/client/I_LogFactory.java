package org.adligo.i.log.client;


public interface I_LogFactory {
	/**
	 * since the apache commons logging stuff wouln't easily port to
	 * CLDC (cell phones), or GWT (Web Browsers JavaScript) this is my solution have a custom
	 * log factory api that can inturn get delegated to the commons
	 * logging factory which can then get delegated to log4j or something
	 * else
	 * 
	 * @param c
	 * @return
	 */
	public I_LogDelegate getLog(Class clazz);
}
