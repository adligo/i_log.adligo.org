package org.adligo.i.log.client;


public class LogMessage {
	private I_LogMutant log = null;
	private Throwable throwable = null;
	private Object message = null;
	private short level = I_LogDelegate.LOG_LEVEL_INFO;
	
	public short getLevel() {
		return level;
	}
	public void setLevel(short level) {
		this.level = level;
	}
	public Object getMessage() {
		return message;
	}
	public void setMessage(Object message) {
		this.message = message;
	}
	
	
	public void dispose() {
		log = null;
		throwable = null;
		message = null;
		level = 0;
	}
	
	public Throwable getThrowable() {
		return throwable;
	}
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
	
	public I_LogMutant getLog() {
		return log;
	}
	public void setLog(I_LogMutant log) {
		this.log = log;
	}
	
	public String getLevelString(){
		return getLevelString(level);
	}
	
	public static String getLevelString(short level) {
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
