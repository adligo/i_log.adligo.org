package org.adligo.i.log.client;

import org.adligo.i.util.client.I_Map;

public interface LogMutant extends Log {
	/** "Trace" level logging. */
	public static final short LOG_LEVEL_TRACE  = 1;
	/** "Debug" level logging. */
	public static final short LOG_LEVEL_DEBUG  = 2;
	/** "Info" level logging. */
	public static final short LOG_LEVEL_INFO   = 3;
	/** "Warn" level logging. */
	public static final short LOG_LEVEL_WARN   = 4;
	/** "Error" level logging. */
	public static final short LOG_LEVEL_ERROR  = 5;
	/** "Fatal" level logging. */
	public static final short LOG_LEVEL_FATAL  = 6;
	/** Enable all logging levels */
	public static final short LOG_LEVEL_ALL    = (LOG_LEVEL_TRACE - 1);
	/** Enable no logging levels */
	public static final short LOG_LEVEL_OFF    = (LOG_LEVEL_FATAL + 1);
	/**
	 * read the properties (file) and set the log
	 * @param props
	 */
	public void setLogLevel(I_Map props);
	/**
	 * one of the levels in this class
	 * @param p
	 */
	public void setLevel(short p);
}
