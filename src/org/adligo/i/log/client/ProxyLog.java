package org.adligo.i.log.client;

import org.adligo.i.util.client.ArrayIterator;
import org.adligo.i.util.client.ClassUtils;
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
public class ProxyLog  implements LogMutant {
	protected Log single_delegate = null;
	private I_Collection delegates = null;
	private Class logClass;
	private short level = LogMutant.LOG_LEVEL_INFO;
	
	public ProxyLog(Class c) {
		 logClass = c;
	}
	
	public synchronized void addDelegate(Log p) {
		if (LogPlatform.log) {
			System.out.println("entering add delegate in ProxyLog " + p + "\n\t level " + p.getLevel() + " for class " + logClass.getName());
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
	
	public I_Iterator getDelegates() {
		if (delegates == null && single_delegate != null) {
			return new ArrayIterator(new Log[] {single_delegate});
		} else if (delegates != null) {
			return delegates.getIterator();
		} else {
			return new ArrayIterator(new Log[] {});
		}
	}

	public void debug(Object message, Throwable t) {
		if (isDebugEnabled()) {
			if (delegates == null && single_delegate != null) {
				single_delegate.debug(message, t);
			} else if (delegates != null) {
				I_Iterator it = delegates.getIterator();
				while (it.hasNext()) {
					Log delegate = (Log) it.next();
					delegate.debug(message, t);
				}
			} 
		}
	}

	public void debug(Object message) {
		if (isDebugEnabled()) {
			if (delegates == null && single_delegate != null) {
				single_delegate.debug(message);
			} else if (delegates != null) {
				I_Iterator it = delegates.getIterator();
				while (it.hasNext()) {
					Log delegate = (Log) it.next();
					delegate.debug(message);
				}
			} 
		}
	}

	public void error(Object message, Throwable t) {
		if (isErrorEnabled()) {
			if (delegates == null && single_delegate != null) {
				single_delegate.error(message, t);
			} else if (delegates != null) {
				I_Iterator it = delegates.getIterator();
				while (it.hasNext()) {
					Log delegate = (Log) it.next();
					delegate.error(message, t);
				}
			} 
		}
	}

	public void error(Object message) {
		if (isErrorEnabled()) {
			if (delegates == null && single_delegate != null) {
				single_delegate.error(message);
			} else if (delegates != null) {
				I_Iterator it = delegates.getIterator();
				while (it.hasNext()) {
					Log delegate = (Log) it.next();
					delegate.error(message);
				}
			} 
		}
	}

	public void fatal(Object message, Throwable t) {
		if (isFatalEnabled()) {
			if (delegates == null && single_delegate != null) {
				single_delegate.fatal(message, t);
			} else if (delegates != null) {
				I_Iterator it = delegates.getIterator();
				while (it.hasNext()) {
					Log delegate = (Log) it.next();
					delegate.fatal(message, t);
				}
			} 
		}
	}

	public void fatal(Object message) {
		if (isFatalEnabled()) {
			if (delegates == null && single_delegate != null) {
				single_delegate.fatal(message);
			} else if (delegates != null) {
				I_Iterator it = delegates.getIterator();
				while (it.hasNext()) {
					Log delegate = (Log) it.next();
					delegate.fatal(message);
				}
			} 
		}
	}

	public void info(Object message, Throwable t) {
		if (isInfoEnabled()) {
			if (delegates == null && single_delegate != null) {
				single_delegate.info(message, t);
			} else if (delegates != null) {
				I_Iterator it = delegates.getIterator();
				while (it.hasNext()) {
					Log delegate = (Log) it.next();
					delegate.info(message, t);
				}
			}
		}
	}

	public void info(Object message) {
		if (isInfoEnabled()) {
			if (delegates == null && single_delegate != null) {
				single_delegate.info(message);
			} else if (delegates != null) {
				I_Iterator it = delegates.getIterator();
				while (it.hasNext()) {
					Log delegate = (Log) it.next();
					delegate.info(message);
				}
			}
		}
	}

	public void setLevel(short p) {
		this.level = p;
	}
	
	public void setLogLevel(I_Map props) {
		if (delegates == null && single_delegate != null) {
			single_delegate.setLogLevel(props);
		} else if (delegates != null){
			I_Iterator it = delegates.getIterator();
			while (it.hasNext()) {
				LogMutant delegate = (LogMutant) it.next();
				delegate.setLogLevel(props);
			}
		}
		this.level = SimpleLog.getLogLevel(props, logClass.getName());
	}
	
	public boolean isDebugEnabled() {
		return SimpleLog.isLevelEnabled(LogMutant.LOG_LEVEL_DEBUG, this.level);
	}

	public boolean isErrorEnabled() {
		return SimpleLog.isLevelEnabled(LogMutant.LOG_LEVEL_ERROR, this.level);
	}

	public boolean isFatalEnabled() {
		return SimpleLog.isLevelEnabled(LogMutant.LOG_LEVEL_FATAL, this.level);
	}

	public boolean isInfoEnabled() {
		return SimpleLog.isLevelEnabled(LogMutant.LOG_LEVEL_INFO, this.level);
	}

	public boolean isTraceEnabled() {
		return SimpleLog.isLevelEnabled(LogMutant.LOG_LEVEL_TRACE, this.level);
	}

	public boolean isWarnEnabled() {
		return SimpleLog.isLevelEnabled(LogMutant.LOG_LEVEL_WARN, this.level);
	}

	public void trace(Object message, Throwable t) {
		if (isTraceEnabled()) {
			if (delegates == null && single_delegate != null) {
				single_delegate.trace(message, t);
			} else if (delegates != null) {
				I_Iterator it = delegates.getIterator();
				while (it.hasNext()) {
					Log delegate = (Log) it.next();
					delegate.trace(message, t);
				}
			} 
		}
	}

	public void trace(Object message) {
		if (isTraceEnabled()) {
			if (delegates == null && single_delegate != null) {
				single_delegate.trace(message);
			} else if (delegates != null) {
				I_Iterator it = delegates.getIterator();
				while (it.hasNext()) {
					Log delegate = (Log) it.next();
					delegate.trace(message);
				}
			} 
		}
	}

	public void warn(Object message, Throwable t) {
		if (isWarnEnabled()) {
			
			if (delegates == null && single_delegate != null) {
				single_delegate.warn(message, t);
			} else if (delegates != null) {
				I_Iterator it = delegates.getIterator();
				while (it.hasNext()) {
					Log delegate = (Log) it.next();
					delegate.warn(message, t);
				}
			} 
		}
	}

	public void warn(Object message) {
		if (isWarnEnabled()) {
			if (delegates == null && single_delegate != null) {
				single_delegate.warn(message);
			} else if (delegates != null) {
				
				
				I_Iterator it = delegates.getIterator();
				
				while (it.hasNext()) {
					Log delegate = (Log) it.next();
					delegate.warn(message);
				}
			}
		}
	}

	public void log(int type, Object message, Throwable t) {
		if (delegates == null && single_delegate != null) {
			if (single_delegate.getLevel() >= type) {
				single_delegate.log(type, message, t);
			}
		} else if (delegates != null) {
			I_Iterator it = delegates.getIterator();
			while (it.hasNext()) {
				Log delegate = (Log) it.next();
				if (delegate.getLevel() >= type) {
					delegate.log(type, message, t);
				}
			}
		} 
	}

	public short getLevel() {
		return level;
	}

	public Class getLogClass() {
		return logClass;
	}

}
