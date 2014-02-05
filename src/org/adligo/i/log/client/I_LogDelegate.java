package org.adligo.i.log.client;


public interface I_LogDelegate extends Log {
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
     * these are just implementaion detail objects
     * these are needed for event base initalization
     * (instead of static block initalization, which has issues on many platforms including;
     * GWT doesn't even have static blocks!
     * J2EE static initalization means you can't pull settings from the Servlet context!
     * J2SE its fairly hard if not impossible to get static initalization to work properly
     *    when its not in the LogFactory, ug this was a pain.
     *    
     * )
     * 
     * Note this method is only called after all the checking has taken place
     * so if your proxyLog is set to Info 
     * this method will not get called for a Debug
     * exc.
     * 
     * @param type
     * @param message
     * @param t
     */
    public void log(short type, Object message, Throwable t);

    
}
