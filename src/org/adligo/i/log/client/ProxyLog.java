package org.adligo.i.log.client;

import org.adligo.i.util.client.ArrayIterator;
import org.adligo.i.util.client.CollectionFactory;
import org.adligo.i.util.client.I_Collection;
import org.adligo.i.util.client.I_Iterator;
import org.adligo.i.util.client.I_Map;

/**
 * this class tracks all logging
 * until it has 
 * received notification that the
 * log levels have been set,
 * at which point it logs everything in order 
 * and then defers new calls to the 
 * a SimpleLog obtained by LogFactory
 * 
 * @author scott
 *
 */
public class ProxyLog  implements I_LogMutant, I_ProxyLog {
	protected I_LogDelegate single_delegate = null;
	private I_Collection delegates = null;
	private String logName;
	private short level = I_LogDelegate.LOG_LEVEL_OFF;
	private boolean enabled = true;
	
	public ProxyLog(Class c) {
		if (c == null) {
			throw new NullPointerException("ProxyLog " +
					"constructor can not accept a null Class");
		}
		logName = c.getName();
	}
	
	public ProxyLog(String name) {
		 if (name == null) {
			 throw new NullPointerException("ProxyLog " +
					"constructor can not accept a String name");
		 }
		 logName = name;
	}
	
	public synchronized void addDelegate(I_LogDelegate p) {
		if (LogPlatform.isDebug()) {
			LogPlatform.log("LogFactory","entering add delegate " + p + 
					" in " + this + " for class " + logName);
		}
		if (p != null) {
			if (single_delegate == null) {
				single_delegate = p;
			} else {
				if (delegates == null) {
					delegates = CollectionFactory.create();
					delegates.add(single_delegate);
				}
				delegates.add(p);
			}
		}
	}
	/**
	 * returns a iterator of 
	 * I_LogDelegates
	 * 
	 * @return
	 */
	public I_Iterator getDelegates() {
		if (delegates == null && single_delegate != null) {
			return new ArrayIterator(new I_LogDelegate[] {single_delegate});
		} else if (delegates != null) {
			return delegates.getIterator();
		} else {
			return new ArrayIterator(new I_LogDelegate[] {});
		}
	}

	public void debug(Object message, Throwable t) {
		if (isDebugEnabled()) {
			if (delegates == null && single_delegate != null) {
				single_delegate.log(I_LogDelegate.LOG_LEVEL_DEBUG, message, t);
				if (LogPlatform.isDebug()) {
					LogPlatform.log("LogFactory","sending to " + single_delegate );
				}
			} else if (delegates != null) {
				I_Iterator it = delegates.getIterator();
				
				while (it.hasNext()) {
					I_LogDelegate delegate = (I_LogDelegate) it.next();
					if (LogPlatform.isDebug()) {
						LogPlatform.log("LogFactory","sending to " + delegate );
					}
					delegate.log(I_LogDelegate.LOG_LEVEL_DEBUG, message, t);
				}
			} 
		}
	}

	public void debug(Object message) {
		if (isDebugEnabled()) {
			if (delegates == null && single_delegate != null) {
				single_delegate.log(I_LogDelegate.LOG_LEVEL_DEBUG, message, null);
			} else if (delegates != null) {
				I_Iterator it = delegates.getIterator();
				while (it.hasNext()) {
					I_LogDelegate delegate = (I_LogDelegate) it.next();
					delegate.log(I_LogDelegate.LOG_LEVEL_DEBUG, message, null);
				}
			} 
		}
	}

	public void error(Object message, Throwable t) {
		if (isErrorEnabled()) {
			if (delegates == null && single_delegate != null) {
				single_delegate.log(I_LogDelegate.LOG_LEVEL_ERROR, message, t);
			} else if (delegates != null) {
				I_Iterator it = delegates.getIterator();
				while (it.hasNext()) {
					I_LogDelegate delegate = (I_LogDelegate) it.next();
					delegate.log(I_LogDelegate.LOG_LEVEL_ERROR, message, t);
				}
			} 
		}
	}

	public void error(Object message) {
		if (isErrorEnabled()) {
			if (delegates == null && single_delegate != null) {
				single_delegate.log(I_LogDelegate.LOG_LEVEL_ERROR, message, null);
			} else if (delegates != null) {
				I_Iterator it = delegates.getIterator();
				while (it.hasNext()) {
					I_LogDelegate delegate = (I_LogDelegate) it.next();
					delegate.log(I_LogDelegate.LOG_LEVEL_ERROR, message, null);
				}
			} 
		}
	}

	public void fatal(Object message, Throwable t) {
		if (isFatalEnabled()) {
			if (delegates == null && single_delegate != null) {
				single_delegate.log(I_LogDelegate.LOG_LEVEL_FATAL, message, t);
			} else if (delegates != null) {
				I_Iterator it = delegates.getIterator();
				while (it.hasNext()) {
					I_LogDelegate delegate = (I_LogDelegate) it.next();
					delegate.log(I_LogDelegate.LOG_LEVEL_FATAL, message, t);
				}
			} 
		}
	}

	public void fatal(Object message) {
		if (isFatalEnabled()) {
			if (delegates == null && single_delegate != null) {
				single_delegate.log(I_LogDelegate.LOG_LEVEL_FATAL, message, null);
			} else if (delegates != null) {
				I_Iterator it = delegates.getIterator();
				while (it.hasNext()) {
					I_LogDelegate delegate = (I_LogDelegate) it.next();
					delegate.log(I_LogDelegate.LOG_LEVEL_FATAL, message, null);
				}
			} 
		}
	}

	public void info(Object message, Throwable t) {
		if (isInfoEnabled()) {
			if (delegates == null && single_delegate != null) {
				single_delegate.log(I_LogDelegate.LOG_LEVEL_INFO, message, t);
			} else if (delegates != null) {
				I_Iterator it = delegates.getIterator();
				while (it.hasNext()) {
					I_LogDelegate delegate = (I_LogDelegate) it.next();
					delegate.log(I_LogDelegate.LOG_LEVEL_INFO, message, t);
				}
			}
		}
	}

	public void info(Object message) {
		if (isInfoEnabled()) {
			if (delegates == null && single_delegate != null) {
				single_delegate.log(I_LogDelegate.LOG_LEVEL_INFO, message, null);
			} else if (delegates != null) {
				I_Iterator it = delegates.getIterator();
				while (it.hasNext()) {
					I_LogDelegate delegate = (I_LogDelegate) it.next();
					delegate.log(I_LogDelegate.LOG_LEVEL_INFO, message, null);
				}
			}
		}
	}

	public void setLevel(short p) {
		this.level = p;
	}
	
	public void setLogLevel(I_Map props) {
		// the delegates don't need a log level
		this.level = SimpleLog.getLogLevel(props, logName);
	}
	
	public boolean isDebugEnabled() {
		return SimpleLog.isLevelEnabled(I_LogDelegate.LOG_LEVEL_DEBUG, this.level);
	}

	public boolean isErrorEnabled() {
		return SimpleLog.isLevelEnabled(I_LogDelegate.LOG_LEVEL_ERROR, this.level);
	}

	public boolean isFatalEnabled() {
		return SimpleLog.isLevelEnabled(I_LogDelegate.LOG_LEVEL_FATAL, this.level);
	}

	public boolean isInfoEnabled() {
		return SimpleLog.isLevelEnabled(I_LogDelegate.LOG_LEVEL_INFO, this.level);
	}

	public boolean isTraceEnabled() {
		return SimpleLog.isLevelEnabled(I_LogDelegate.LOG_LEVEL_TRACE, this.level);
	}

	public boolean isWarnEnabled() {
		return SimpleLog.isLevelEnabled(I_LogDelegate.LOG_LEVEL_WARN, this.level);
	}

	public void trace(Object message, Throwable t) {
		if (isTraceEnabled()) {
			if (delegates == null && single_delegate != null) {
				single_delegate.log(I_LogDelegate.LOG_LEVEL_TRACE, message, t);
			} else if (delegates != null) {
				I_Iterator it = delegates.getIterator();
				while (it.hasNext()) {
					I_LogDelegate delegate = (I_LogDelegate) it.next();
					delegate.log(I_LogDelegate.LOG_LEVEL_TRACE, message, t);
				}
			} 
		}
	}

	public void trace(Object message) {
		if (isTraceEnabled()) {
			if (delegates == null && single_delegate != null) {
				single_delegate.log(I_LogDelegate.LOG_LEVEL_TRACE, message, null);
			} else if (delegates != null) {
				I_Iterator it = delegates.getIterator();
				while (it.hasNext()) {
					I_LogDelegate delegate = (I_LogDelegate) it.next();
					delegate.log(I_LogDelegate.LOG_LEVEL_TRACE, message, null);
				}
			} 
		}
	}

	public void warn(Object message, Throwable t) {
		if (isWarnEnabled()) {
			
			if (delegates == null && single_delegate != null) {
				single_delegate.log(I_LogDelegate.LOG_LEVEL_WARN, message, t);
			} else if (delegates != null) {
				I_Iterator it = delegates.getIterator();
				while (it.hasNext()) {
					I_LogDelegate delegate = (I_LogDelegate) it.next();
					delegate.log(I_LogDelegate.LOG_LEVEL_WARN, message, t);
				}
			} 
		}
	}

	public void warn(Object message) {
		if (isWarnEnabled()) {
			if (delegates == null && single_delegate != null) {
				single_delegate.log(I_LogDelegate.LOG_LEVEL_WARN, message, null);
			} else if (delegates != null) {
				I_Iterator it = delegates.getIterator();
				while (it.hasNext()) {
					I_LogDelegate delegate = (I_LogDelegate) it.next();
					delegate.log(I_LogDelegate.LOG_LEVEL_WARN, message, null);
				}
			}
		}
	}

	public short getLevel() {
		return level;
	}

	public String getLogName() {
		return logName;
	}

	public void log(short type, Object message, Throwable t) {
		if (LogPlatform.isDebug()) {
			LogPlatform.log("LogFactory"," in log with type " + type +
					" isEnabled " + isEnabled());
		}
		if (isEnabled()) {
			switch (type) {
				case I_LogDelegate.LOG_LEVEL_TRACE:
					this.trace(message, t);
					break;
				case I_LogDelegate.LOG_LEVEL_DEBUG:
					this.debug(message, t);
					break;
				case I_LogDelegate.LOG_LEVEL_INFO:
					this.info(message, t);
					break;
				case I_LogDelegate.LOG_LEVEL_WARN:
					this.warn(message, t);
					break;
				case I_LogDelegate.LOG_LEVEL_ERROR:
					this.error(message, t);
					break;
				case I_LogDelegate.LOG_LEVEL_FATAL:
					this.fatal(message, t);
					break;
			}
		}
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((logName == null) ? 0 : logName.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProxyLog other = (ProxyLog) obj;
		if (logName == null) {
			if (other.logName != null)
				return false;
		} else if (!logName.equals(other.logName))
			return false;
		return true;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ProxyLog [name=");
		sb.append(logName);
		sb.append(",delegates=");
		sb.append(this.delegates);
		sb.append("]");
		return sb.toString();
	}
}
