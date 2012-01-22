package org.adligo.i.log.client;

import org.adligo.i.log.client.models.LogMessage;
import org.adligo.i.util.client.ClassUtils;
import org.adligo.i.util.client.I_ImmutableMap;

/**
 * this class simply sends a message out to the 
 * I_LogDispatcher delegate, which makes re implementing a adligo Log a lot simpler
 * and so simplyfies this whole project
 * 
 * This is the preferred way to delegate log messages
 * 
 * @author scott
 *
 */
public class SingleDispatchLog extends SimpleLog implements Log {
	
	public SingleDispatchLog(Class clazz, I_ImmutableMap props) {
		this(ClassUtils.getClassName(clazz), props);
	}
	
	public SingleDispatchLog(String clazz, I_ImmutableMap props) {
		super(clazz, props);
	}
	
    public void log(short type, Object message, Throwable t) {
    	LogMessage logMessage = createLogMessage(type, message, t);
    	I_LogDispatcher dispatcher = LogPlatform.getDispatcher();

    	if (LogPlatform.isDebug()) {
    		LogPlatform.log("LogFactory","dispatcher "  +
    				dispatcher);
    	}
        if (dispatcher != null) {
        	dispatcher.dispatch(logMessage);
        }
    }
  
}
