package org.adligo.i.log.client.models;

import org.adligo.i.log.client.I_LogDelegate;


public abstract class LogMessage {
	private String name = "";
	protected String stackAsString = "";
	private boolean filledStackAsString = false;
	
	private Throwable throwable = null;
	private short level = I_LogDelegate.LOG_LEVEL_INFO;
	private Integer windowId;

	protected LogMessage() {
		
	}
	
	public short getLevel() {
		return level;
	}
	public void setLevel(short level) {
		this.level = level;
	}

	public Throwable getThrowable() {
		return throwable;
	}
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	
	public String getLevelString(){
		return getLevelString(level);
	}
	
	
	public Integer getWindowId() {
		return windowId;
	}

	public void setWindowId(Integer windowId) {
		this.windowId = windowId;
	}
	
	public static String fillInStack(Throwable throwable) {
		if (throwable != null) {
			StringBuilder buf = new StringBuilder();
			// Append stack trace if not null
	        if(throwable != null) {
	        	StackTraceElement [] trace = throwable.getStackTrace();
	        	buf.append(" <");
	            buf.append(throwable.toString());
	            buf.append(">");

	            buf.append("\n");
	           
	            
	            for (int j = 0; j < trace.length; j++) {
	            	buf.append("\t at ");
	            	buf.append(trace[j].toString());
	            	buf.append("\n");
				}
	           
	            return buf.toString();
	        }
		}
		return "";
	}
	public void fillInStack() {
		if (!filledStackAsString) {
			stackAsString = fillInStack(throwable);
			filledStackAsString = true;
		}
	}
	
	public abstract Object getMessage();
	public abstract void setMessage(Object o);
	
	public static String getLevelString(short level) {
		return getLevelString(level, true);
	}
	public static String getLevelString(short level, boolean forMessage) {
		if (forMessage) {
			switch (level) {
				case I_LogDelegate.LOG_LEVEL_TRACE:
					return "[TRACE] ";
				case I_LogDelegate.LOG_LEVEL_DEBUG:
					return "[DEBUG] ";
				case I_LogDelegate.LOG_LEVEL_INFO:
					return "[INFO] ";
				case I_LogDelegate.LOG_LEVEL_WARN:
					return "[WARN] ";
				case I_LogDelegate.LOG_LEVEL_ERROR:
					return "[ERROR] ";
				case I_LogDelegate.LOG_LEVEL_FATAL:
					return "[FATAL] ";
				case I_LogDelegate.LOG_LEVEL_ALL:
					return "[ALL] ";
				case I_LogDelegate.LOG_LEVEL_OFF:
					return "[OFF] ";
				default:
					return "[UNKNOWN_LEVEL] ";
			}
		} else {
			switch (level) {
				case I_LogDelegate.LOG_LEVEL_TRACE:
					return "TRACE";
				case I_LogDelegate.LOG_LEVEL_DEBUG:
					return "DEBUG";
				case I_LogDelegate.LOG_LEVEL_INFO:
					return "INFO";
				case I_LogDelegate.LOG_LEVEL_WARN:
					return "WARN";
				case I_LogDelegate.LOG_LEVEL_ERROR:
					return "ERROR";
				case I_LogDelegate.LOG_LEVEL_FATAL:
					return "FATAL";
				case I_LogDelegate.LOG_LEVEL_ALL:
					return "ALL";
				case I_LogDelegate.LOG_LEVEL_OFF:
					return "OFF";
				default:
					return "UNKNOWN_LEVEL";
			}
		}
	}
}
