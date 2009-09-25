package org.adligo.i.log.client;

import org.adligo.i.log.client.models.LogMessage;
import org.adligo.i.log.client.models.LogMessageFactory;
import org.adligo.i.util.client.ClassUtils;
import org.adligo.i.util.client.I_Map;
import org.adligo.i.util.client.StringUtils;

public class NetLog extends SimpleLog implements Log {
	private String baseUrl = null;
	private static Integer windowId = null;
	private static int requestId = 0;
	
	public NetLog(Class clazz, I_Map props) {
		this(ClassUtils.getClassName(clazz), props);
	}
	
	public NetLog(String clazz, I_Map props) {
		super(clazz, props);
		baseUrl = (String) props.get(LogPlatform.NET_LOG_URL);
		if (StringUtils.isEmpty(baseUrl)) {
			baseUrl = "/log";
		}
	}
	
    public void log(short type, Object message, Throwable t) {
    	
    	String name = "";
        // Append the name of the log instance if so configured
        if( showShortName) {
        	if (showLogName) {
	            if( shortLogName==null ) {
	                // Cut all but the last component of the name for both styles
	                shortLogName = logName.substring(logName.lastIndexOf('.') + 1);
	                shortLogName =
	                    shortLogName.substring(shortLogName.lastIndexOf('/') + 1);
	            }
	            name = shortLogName;
        	}
        } else if(showLogName) {
        	name = this.logName;
        }

        StringBuffer buf = new StringBuffer();
        SimpleLog.createLogMessage(message, t, buf);
        
        LogMessage logMessage = LogMessageFactory.createMessage(buf.toString());
        logMessage.setLevel(type);
        logMessage.setName(name);
        logMessage.setThrowable(t);
        logMessage.setWindowId(NetLog.windowId);
        
        I_LogDispatcher dispatcher = LogPlatform.getDispatcher();
        if (dispatcher != null) {
        	dispatcher.dispatch(logMessage);
        }
    }


	public static Integer getWindowId() {
		return windowId;
	}

	public static void setWindowId(int windowId) {
		NetLog.windowId = new Integer(windowId);
	}
}
