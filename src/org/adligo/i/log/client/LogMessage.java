package org.adligo.i.log.client;


public class LogMessage {
	private Log log = null;
	private Throwable throwable = null;
	private Object message = null;
	private short level = LogMutant.LOG_LEVEL_INFO;
	
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
	
	public Log getLog() {
		return log;
	}
	public void setLog(Log log) {
		this.log = log;
	}
}
