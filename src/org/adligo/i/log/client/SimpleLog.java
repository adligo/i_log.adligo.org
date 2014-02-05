/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 


package org.adligo.i.log.client;


import org.adligo.i.log.client.models.LogMessage;
import org.adligo.i.log.client.models.LogMessageFactory;
import org.adligo.i.util.client.AppenderFactory;
import org.adligo.i.util.client.I_ImmutableMap;
import org.adligo.i.util.client.I_Iterator;
import org.adligo.i.util.client.I_Appender;
import org.adligo.i.util.client.I_SystemOutput;
import org.adligo.i.util.client.StringUtils;
import org.adligo.i.util.client.SystemOutput;
import org.adligo.i.util.client.ThrowableHelperFactory;

/**
 * 
 * A copy of the apache commons SimpleLog class, with 
 * date support removed, for CLDC 2.0
 */
public class SimpleLog implements I_LogMutant, I_LogDelegate {
	
    // ------------------------------------------------------- Class Attributes

    /** All system properties used by <code>SimpleLog</code> start with this */
    static protected final String systemPrefix = "";
    /**
     * redirect output for testing
     */
    static protected I_SystemOutput out = SystemOutput.INSTANCE;
    /** The name of this simple log instance */
    protected final String logName;
    /** The current log level */
    protected volatile short currentLogLevel;
    
    private boolean enabled = true;



    /**
     * Construct a simple log with given name.
     *
     * @param name log name
     */
    public SimpleLog(String name, I_ImmutableMap props) {

        logName = name;

        // Set initial log level
        // Used to be: set default log level to ERROR
        // IMHO it should be lower, but at least info ( costin ).
        setLevel(I_LogDelegate.LOG_LEVEL_INFO);

        setLogLevel(props);
    }
    



    // ---------------------------------------------------- Log Level Constants


    private static String getStringProperty(I_ImmutableMap props, String name) {
    	if (props == null) {
    		return null;
    	}

    	String toRet = (String) props.get(name);
    	if (LogPlatform.isDebug()) {
			LogPlatform.log("SimpleLog", "getStringProperty(" + name + ") returns " + toRet);
		}
        return toRet;
    }

	public static short getLogLevel(I_ImmutableMap props, String logName) {
		// Set log level from properties
        String lvl = getStringProperty(props, logName);
        
        String tempName = logName;
        int i = String.valueOf(tempName).lastIndexOf('.');
        while(null == lvl && i > -1) {
        	tempName = tempName.substring(0,i);
            lvl = getStringProperty(props, tempName);
            i = String.valueOf(tempName).lastIndexOf('.');
        }

        if(null == lvl) {
            lvl =  getStringProperty(props,systemPrefix + "defaultlog");
        }

        if(StringUtils.equalsIgnoreCase("all",lvl)) {
            return I_LogDelegate.LOG_LEVEL_ALL;
        } else if(StringUtils.equalsIgnoreCase("trace",lvl)) {
        	return I_LogDelegate.LOG_LEVEL_TRACE;
        } else if(StringUtils.equalsIgnoreCase("debug",lvl)) {
        	return I_LogDelegate.LOG_LEVEL_DEBUG;
        } else if(StringUtils.equalsIgnoreCase("info",lvl)) {
        	return I_LogDelegate.LOG_LEVEL_INFO;
        } else if(StringUtils.equalsIgnoreCase("warn",lvl)) {
        	return I_LogDelegate.LOG_LEVEL_WARN;
        } else if(StringUtils.equalsIgnoreCase("error",lvl)) {
        	return I_LogDelegate.LOG_LEVEL_ERROR;
        } else if(StringUtils.equalsIgnoreCase("fatal",lvl)) {
        	return I_LogDelegate.LOG_LEVEL_FATAL;
        } else if(StringUtils.equalsIgnoreCase("off",lvl)) {
        	return I_LogDelegate.LOG_LEVEL_OFF;
        }
        return I_LogDelegate.LOG_LEVEL_INFO;
        
        //System.out.println("Log " + name  + " is set to " + getLevel());
	}


    // -------------------------------------------------------- Properties

    /**
     * <p> Set logging level. </p>
     *
     * @param currentLogLevel new logging level
     */
    public void setLevel(short currentLogLevel) {
    	this.currentLogLevel = currentLogLevel;
    }


    /**
     * <p> Get logging level. </p>
     */
    public short getLevel() {
    	return currentLogLevel;
    }


    // -------------------------------------------------------- Logging Methods


    /**
     * <p> Do the actual logging.
     * This method assembles the message
     * and then calls <code>write()</code> to cause it to be written.</p>
     *
     * @param type One of the LOG_LEVEL_XXX constants defining the log level
     * @param message The message itself (typically a String)
     * @param t The exception whose stack trace should be logged
     */
    public void log(short type, Object message, Throwable t) {
    	LogMessage logMessage = createLogMessage(type, message, t);
    	
        I_Formatter fmt = LogPlatform.getFormatter();
        out.out(fmt.format(logMessage));
    }
    
    public LogMessage createLogMessage(short type, Object message, Throwable t) {
    	 
       	I_Appender buf = AppenderFactory.create();
        SimpleLog.createLogMessage(message, t, buf);
        
        LogMessage logMessage = LogMessageFactory.createMessage(buf.toString());
        logMessage.setLevel(type);
        logMessage.setName(logName);
        logMessage.setThrowable(t);
        
        return logMessage;
    }

    /**
     * Note GWT appears to have implement 
     * Throwable getStackTrace()
     * by always returning 0 StackTraceElements in the actual javascript
     * so i_log can't get a stacktrace when running in a actual browser
     */
	public static void createLogMessage(Object message, Throwable t, I_Appender p) {
		
		createLogMessage(SimpleFormatter.DEFAULT_PRE_TEXT, message, t, p, SimpleFormatter.DEFAULT_LINE_FEED);
	}

	/**
	 * 
	 * @param preText allows for tabs (\t) to indent messages
	 * @param message
	 * @param t
	 * @param buf
	 * @param lineFeed
	 */
	public static void createLogMessage(String preText, Object message, Throwable t, I_Appender buf, String lineFeed) {
		// Append the message
		buf.append(preText);
		buf.append(String.valueOf(message));
        buf.append(lineFeed);
        ThrowableHelperFactory.appendStackTracesString(preText, t, lineFeed, buf);
	}


    /**
     * Is the given log level currently enabled?
     *
     * @param logLevel is this level enabled?
     */
    protected boolean isLevelEnabled(int logLevel) {
        return (logLevel >= currentLogLevel);
    }


    // -------------------------------------------------------- Log Implementation


    /**
     * Logs a message with 
     * <code>org.apache.commons.logging.impl.SimpleLog.LOG_LEVEL_DEBUG</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#debug(Object)
     */
    public final void debug(Object message) {
    	if (this.isLevelEnabled(I_LogDelegate.LOG_LEVEL_DEBUG)) {
    		log(I_LogDelegate.LOG_LEVEL_DEBUG, message, null);
    	}
    }


    /**
     * Logs a message with 
     * <code>org.apache.commons.logging.impl.SimpleLog.LOG_LEVEL_DEBUG</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#debug(Object, Throwable)
     */
    public final void debug(Object message, Throwable t) {
    	if (this.isLevelEnabled(I_LogDelegate.LOG_LEVEL_DEBUG)) {
    		log(I_LogDelegate.LOG_LEVEL_DEBUG, message, t);
    	}
    }


    /**
     * Logs a message with 
     * <code>org.apache.commons.logging.impl.SimpleLog.LOG_LEVEL_TRACE</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#trace(Object)
     */
    public final void trace(Object message) {
    	if (this.isLevelEnabled(I_LogDelegate.LOG_LEVEL_TRACE)) {
    		log(I_LogDelegate.LOG_LEVEL_TRACE, message, null);
    	}
    }


    /**
     * Logs a message with 
     * <code>org.apache.commons.logging.impl.SimpleLog.LOG_LEVEL_TRACE</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#trace(Object, Throwable)
     */
    public final void trace(Object message, Throwable t) {
    	if (this.isLevelEnabled(I_LogDelegate.LOG_LEVEL_TRACE)) {
    		log(I_LogDelegate.LOG_LEVEL_TRACE, message, t);
    	}
    }


    /**
     * Logs a message with 
     * <code>org.apache.commons.logging.impl.SimpleLog.LOG_LEVEL_INFO</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#info(Object)
     */
    public final void info(Object message) {
    	if (this.isLevelEnabled(I_LogDelegate.LOG_LEVEL_INFO)) {
    		log(I_LogDelegate.LOG_LEVEL_INFO,message,null);
    	}
    }


    /**
     * Logs a message with 
     * <code>org.apache.commons.logging.impl.SimpleLog.LOG_LEVEL_INFO</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#info(Object, Throwable)
     */
    public final void info(Object message, Throwable t) {
    	if (this.isLevelEnabled(I_LogDelegate.LOG_LEVEL_INFO)) {
    		log(I_LogDelegate.LOG_LEVEL_INFO, message, t);
    	}
    }


    /**
     * Logs a message with 
     * <code>org.apache.commons.logging.impl.SimpleLog.LOG_LEVEL_WARN</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#warn(Object)
     */
    public final void warn(Object message) {
    	if (this.isLevelEnabled(I_LogDelegate.LOG_LEVEL_WARN)) {
    		log(I_LogDelegate.LOG_LEVEL_WARN, message, null);
    	}
    }


    /**
     * Logs a message with 
     * <code>org.apache.commons.logging.impl.SimpleLog.LOG_LEVEL_WARN</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#warn(Object, Throwable)
     */
    public final void warn(Object message, Throwable t) {
    	if (this.isLevelEnabled(I_LogDelegate.LOG_LEVEL_WARN)) {
    		log(I_LogDelegate.LOG_LEVEL_WARN, message, t);
    	}
    }


    /**
     * Logs a message with 
     * <code>org.apache.commons.logging.impl.SimpleLog.LOG_LEVEL_ERROR</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#error(Object)
     */
    public final void error(Object message) {
    	if (this.isLevelEnabled(I_LogDelegate.LOG_LEVEL_ERROR)) {
    		log(I_LogDelegate.LOG_LEVEL_ERROR, message, null);
    	}
    }


    /**
     * Logs a message with 
     * <code>org.apache.commons.logging.impl.SimpleLog.LOG_LEVEL_ERROR</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#error(Object, Throwable)
     */
    public final void error(Object message, Throwable t) {
    	if (this.isLevelEnabled(I_LogDelegate.LOG_LEVEL_ERROR)) {
    		log(I_LogDelegate.LOG_LEVEL_ERROR, message, t);
    	}
    }


    /**
     * Log a message with 
     * <code>org.apache.commons.logging.impl.SimpleLog.LOG_LEVEL_FATAL</code>.
     *
     * @param message to log
     * @see org.apache.commons.logging.Log#fatal(Object)
     */
    public final void fatal(Object message) {
    	if (this.isLevelEnabled(I_LogDelegate.LOG_LEVEL_FATAL)) {
    		log(I_LogDelegate.LOG_LEVEL_FATAL, message, null);
    	}
    }


    /**
     * Logs a message with 
     * <code>org.apache.commons.logging.impl.SimpleLog.LOG_LEVEL_FATAL</code>.
     *
     * @param message to log
     * @param t log this cause
     * @see org.apache.commons.logging.Log#fatal(Object, Throwable)
     */
    public final void fatal(Object message, Throwable t) {
    	if (this.isLevelEnabled(I_LogDelegate.LOG_LEVEL_FATAL)) {
    		log(I_LogDelegate.LOG_LEVEL_FATAL, message, t);
    	}
    }


    /**
     * <p> Are debug messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public final boolean isDebugEnabled() {
    	return isLevelEnabled(I_LogDelegate.LOG_LEVEL_DEBUG);
    }


    /**
     * <p> Are error messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public final boolean isErrorEnabled() {
    	return isLevelEnabled(I_LogDelegate.LOG_LEVEL_ERROR);
    }


    /**
     * <p> Are fatal messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public final boolean isFatalEnabled() {
    	return isLevelEnabled(I_LogDelegate.LOG_LEVEL_FATAL);
    }


    /**
     * <p> Are info messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public final boolean isInfoEnabled() {
    	return isLevelEnabled(I_LogDelegate.LOG_LEVEL_INFO);
    }


    /**
     * <p> Are trace messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public final boolean isTraceEnabled() {
    	return isLevelEnabled(I_LogDelegate.LOG_LEVEL_TRACE);
    }


    /**
     * <p> Are warn messages currently enabled? </p>
     *
     * <p> This allows expensive operations such as <code>String</code>
     * concatenation to be avoided when the message will be ignored by the
     * logger. </p>
     */
    public final boolean isWarnEnabled() {
    	return isLevelEnabled(I_LogDelegate.LOG_LEVEL_WARN);
    }


    public static final short getLevel(Log p) {
    	
        if (p.isFatalEnabled()) {
        	return I_LogDelegate.LOG_LEVEL_FATAL;
        }
        if (p.isErrorEnabled()) {
        	return I_LogDelegate.LOG_LEVEL_ERROR;
        }
        if (p.isWarnEnabled()) {
        	return I_LogDelegate.LOG_LEVEL_WARN;
        }
        if (p.isInfoEnabled()) {
        	return I_LogDelegate.LOG_LEVEL_INFO;
        }
        if (p.isDebugEnabled()) {
        	return I_LogDelegate.LOG_LEVEL_DEBUG;
        }
        if (p.isTraceEnabled()) {
    		return I_LogDelegate.LOG_LEVEL_TRACE;
    	}
        
        
        
        
        return -1;
    }

    public void setLogLevel(I_ImmutableMap p) {
    	this.setLevel(SimpleLog.getLogLevel(p, logName));
    }
    
    public static boolean isLevelEnabled(int logLevel, int currentLevel) {
        return (logLevel >= currentLevel);
    }
    

    public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}


	protected static void setOut(I_SystemOutput p_out) {
		out = p_out;
	}

	public void addDelegate(I_LogDelegate p) {
		// TODO Auto-generated method stub
		
	}

	public I_Iterator getDelegates() {
		// TODO Auto-generated method stub
		return null;
	}
}

