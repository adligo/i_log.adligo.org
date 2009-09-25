package org.adligo.i.log.client;

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

        if (windowId != null) {
        	url.append(LogUrl.WINDOW_ID, "" + NetLog.windowId);
        }
        
        
        I_NetLogDispatcher dispatcher = LogPlatform.getDispatcher();
        if (dispatcher != null) {
        	dispatcher.dispatch(url);
        }
    }


	public static Integer getWindowId() {
		return windowId;
	}

	public static void setWindowId(int windowId) {
		NetLog.windowId = new Integer(windowId);
	}
}
