package org.adligo.i.log.shared.models;

import org.adligo.i.log.shared.I_LogDelegate;
import org.adligo.i.log.shared.LogPlatform;
import org.adligo.i.log.shared.SimpleFormatter;
import org.adligo.i.util.shared.AppenderFactory;
import org.adligo.i.util.shared.ClassUtils;
import org.adligo.i.util.shared.I_Appender;
import org.adligo.i.util.shared.I_ThreadContainer;
import org.adligo.i.util.shared.I_ThreadPopulator;
import org.adligo.i.util.shared.ThrowableHelperFactory;


public abstract class LogMessage implements I_LogMessage, I_ThreadContainer {
	private String name = "";
	protected String stackAsString = "";
	private boolean filledStackAsString = false;
	
	private Throwable throwable = null;
	private short level = I_LogDelegate.LOG_LEVEL_INFO;
	private Integer windowId;
	private String thread;
	
	public LogMessage() {
		I_ThreadPopulator tp =  LogPlatform.getThreadPopulator();
		if (tp != null) {
			tp.populateThread(this);
		}
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
	
	public void fillInStack() {
		if (!filledStackAsString) {
			I_Appender out = AppenderFactory.create();
			ThrowableHelperFactory.appendStackTracesString(
					SimpleFormatter.DEFAULT_PRE_TEXT,
					throwable, AppenderFactory.lineSeperator() , out);
			stackAsString = out.toString();
			filledStackAsString = true;
		}
	}
	
	public String getThread() {
		return thread;
	}

	public void setThread(String thread) {
		this.thread = thread;
	}

	
	public abstract Object getMessage();
	public abstract void setMessage(Object o);
	
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
	
	public static void copy(LogMessage target, I_LogMessage source) {
		target.setLevel(source.getLevel());
		target.setThrowable(source.getThrowable());
		target.setName(source.getName());
		target.setWindowId(source.getWindowId());
		target.setThread(source.getThread());
	}
	
	public String toString() {
		I_Appender sb = AppenderFactory.create();
		sb.append(ClassUtils.getClassShortName(LogMessage.class) );
		sb.append(" [name=");
		sb.append(name);
		sb.append(AppenderFactory.lineSeperator());
		sb.append(",message=");
		sb.append(AppenderFactory.lineSeperator());
		Object message = getMessage();
		if (message == null) {
			sb.append("null");
		} else {
			sb.append(getMessage().toString());
		}
		sb.append(AppenderFactory.lineSeperator());
		sb.append(",level=");
		sb.append(getLevelString());
		sb.append("] ");
		sb.append(name);
		return sb.toString();
	}
	
}
