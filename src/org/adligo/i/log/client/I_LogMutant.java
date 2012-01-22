package org.adligo.i.log.client;

import org.adligo.i.util.client.I_ImmutableMap;

public interface I_LogMutant extends Log {
	
	public short getLevel();
	/**
	 * read the properties (file) and set the log
	 * @param props
	 */
	public void setLogLevel(I_ImmutableMap props);
	/**
	 * one of the levels in this class
	 * @param p
	 */
	public void setLevel(short p);
    
}
