package org.adligo.i.log.client;

import org.adligo.i.util.client.I_Map;

public interface I_LogFactory {
	/**
	 * since the apache commons logging stuff wouln't easily port to
	 * CLDC (cell phones), this is my solution have a custom
	 * log factory api that can inturn get delegated to the commons
	 * logging factory which can then get delegated to log4j or something
	 * else
	 * 
	 * @param c
	 * @return
	 */
	public Log getLog(Class clazz);
}