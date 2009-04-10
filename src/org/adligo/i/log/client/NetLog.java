package org.adligo.i.log.client;

import org.adligo.i.util.client.ClassUtils;
import org.adligo.i.util.client.I_Map;

public class NetLog extends SimpleLog implements Log {
	private String logClass = null;
	private String baseUrl = null;
	
	public NetLog(Class clazz, I_Map props) {
		this(ClassUtils.getClassName(clazz), props);
	}
	
	public NetLog(String clazz, I_Map props) {
		super(clazz, props);
		logClass = clazz;
		baseUrl = (String) props.get(LogPlatform.NET_LOG_URL) + "base";
	}
	
    public void log(int type, Object message, Throwable t) {
    	LogUrl url = new LogUrl(baseUrl, false);

        url.append(LogUrl.LEVEL, type);

        // Append the name of the log instance if so configured
        if( showShortName) {
            if( shortLogName==null ) {
                // Cut all but the last component of the name for both styles
                shortLogName = logName.substring(logName.lastIndexOf('.') + 1);
                shortLogName =
                    shortLogName.substring(shortLogName.lastIndexOf('/') + 1);
            }
            url.append(LogUrl.NAME, shortLogName);
        } else if(showLogName) {
        	url.append(LogUrl.NAME,logName);
        }

        StringBuffer buf = new StringBuffer();
        SimpleLog.createLogMessage(message, t, buf);
        url.append(LogUrl.MESSAGE, buf.toString());
        
        I_NetLogDispatcher dispatcher = LogPlatform.getDispatcher();
        if (dispatcher != null) {
        	dispatcher.dispatch(url.toString());
        }
    }
}
