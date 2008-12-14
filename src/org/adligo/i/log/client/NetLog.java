package org.adligo.i.log.client;

import org.adligo.i.util.client.ClassUtils;
import org.adligo.i.util.client.I_Map;

public class NetLog extends SimpleLog implements Log {
	private Class logClass = null;
	private String baseUrl = null;
	
	public NetLog(Class clazz ) {
		super(ClassUtils.getClassName(clazz), LogPlatform.getProps());
		logClass = logClass;
		I_Map props = LogPlatform.getProps();
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

        // Append the message
        url.append(LogUrl.MESSAGE, String.valueOf(message));

        // Append stack trace if not null
        if(t != null)  {
            url.append(LogUrl.STACK, t.toString());
        }
        I_NetLogDispatcher dispatcher = LogPlatform.getDispatcher();
        if (dispatcher != null) {
        	dispatcher.dispatch(url.toString());
        }
    }
}
