package org.adligo.i.log.client;


public class DeferredLogMessage {
	private Class logClass = null;
	private Throwable throwable = null;
	private Object message = null;
	private short level = LogMutant.LOG_LEVEL_INFO;
	
	protected short getLevel() {
		return level;
	}
	protected void setLevel(short level) {
		this.level = level;
	}
	protected Object getMessage() {
		return message;
	}
	protected void setMessage(Object message) {
		this.message = message;
	}
	
	
	protected void dispose() {
		logClass = null;
		throwable = null;
		message = null;
		level = 0;
	}
	
	protected Class getLogClass() {
		return logClass;
	}
	protected void setLogClass(Class logClass) {
		this.logClass = logClass;
	}
	protected Throwable getThrowable() {
		return throwable;
	}
	protected void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}
}
