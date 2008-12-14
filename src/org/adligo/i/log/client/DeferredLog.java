package org.adligo.i.log.client;

import org.adligo.i.util.client.ArrayCollection;
import org.adligo.i.util.client.I_Collection;

public class DeferredLog extends ProxyLog {
	protected static I_Collection deferredMessages = new ArrayCollection();
	
	public DeferredLog(Class clazz) {
		super(clazz);
	}
	
	public void debug(Object message, Throwable t) {
		if (super.single_delegate == null) {
			deferredMessages.add(createMessage(LogMutant.LOG_LEVEL_DEBUG, message, t));
		} else {
			super.debug(message, t);
		}
	}

	public void debug(Object message) {
		if (super.single_delegate == null) {
			deferredMessages.add(createMessage(LogMutant.LOG_LEVEL_DEBUG, message, null));
		} else {
			super.debug(message);
		}
	}

	public void error(Object message, Throwable t) {
		if (super.single_delegate == null) {
			deferredMessages.add(createMessage(LogMutant.LOG_LEVEL_ERROR, message, t));
		} else {
			super.error(message, t);
		}
	}

	public void error(Object message) {
		if (super.single_delegate == null) {
			deferredMessages.add(createMessage(LogMutant.LOG_LEVEL_ERROR, message, null));
		} else {
			super.error(message);
		}
	}

	public void fatal(Object message, Throwable t) {
		if (super.single_delegate == null) {
			deferredMessages.add(createMessage(LogMutant.LOG_LEVEL_FATAL, message, t));
		} else {
			super.fatal(message, t);
		}
	}

	public void fatal(Object message) {
		if (super.single_delegate == null) {
			deferredMessages.add(createMessage(LogMutant.LOG_LEVEL_FATAL, message, null));
		} else {
			super.fatal(message);
		}
	}

	public void info(Object message, Throwable t) {
		if (super.single_delegate == null) {
			deferredMessages.add(createMessage(LogMutant.LOG_LEVEL_INFO, message, t));
		} else {
			super.info(message, t);
		}
	}

	public void info(Object message) {
		if (super.single_delegate == null) {
			deferredMessages.add(createMessage(LogMutant.LOG_LEVEL_INFO, message, null));
		} else {
			super.info(message);
		}
	}

	public void trace(Object message, Throwable t) {
		if (super.single_delegate == null) {
			deferredMessages.add(createMessage(LogMutant.LOG_LEVEL_TRACE, message, t));
		} else {
			super.trace(message, t);
		}
	}

	public void trace(Object message) {
		if (super.single_delegate == null) {
			deferredMessages.add(createMessage(LogMutant.LOG_LEVEL_TRACE, message, null));
		} else {
			super.trace(message);
		}
	}

	public void warn(Object message, Throwable t) {
		if (super.single_delegate == null) {
			deferredMessages.add(createMessage(LogMutant.LOG_LEVEL_WARN, message, t));
		} else {
			super.warn(message, t);
		}
	}

	public void warn(Object message) {
		if (super.single_delegate == null) {
			deferredMessages.add(createMessage(LogMutant.LOG_LEVEL_WARN, message, null));
		} else {
			super.warn(message);
		}
	}

	private LogMessage createMessage(short level, Object message, Throwable t) {
		LogMessage toRet = new LogMessage();
		toRet.setLevel(level);
		toRet.setMessage(message);
		toRet.setThrowable(t);
		toRet.setLog(this);
		return toRet;
	}
	
	protected void consumeMessage(LogMessage p) {
		super.log(p.getLevel(), p.getMessage(), p.getThrowable());
	}
	
}