package org.adligo.i.log.client;

import org.adligo.i.util.client.ClassUtils;
import org.adligo.i.util.client.I_Map;

public class NetLog extends SimpleLog implements Log {
	private String baseUrl = null;
	private static String jsessionid = null;
	private static Integer windowId = null;
	private static int requestId = 0;
	
	public NetLog(Class clazz, I_Map props) {
		this(ClassUtils.getClassName(clazz), props);
	}
	
	public NetLog(String clazz, I_Map props) {
		super(clazz, props);
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
        if (jsessionid != null) {
        	url.append(LogUrl.JSESSION_ID, NetLog.jsessionid);
        }
        if (windowId != null) {
        	url.append(LogUrl.WINDOW_ID, "" + NetLog.windowId);
        }
        
        //pretty lame GWT is auto cacheing requests
        // and doesn't have a api to get around it
        // who knows what other client apis (JREs including J2ME) may have, so leave this in
        url.append(LogUrl.REQUEST_ID, "" + requestId);
        synchronized (NetLog.class) {
        	requestId++;
		}
        
        I_NetLogDispatcher dispatcher = LogPlatform.getDispatcher();
        if (dispatcher != null) {
        	dispatcher.dispatch(url.toString());
        }
    }

	public static String getJsessionid() {
		return jsessionid;
	}

	public static void setJsessionid(String jsessionid) {
		NetLog.jsessionid = jsessionid;
	}

	public static Integer getWindowId() {
		return windowId;
	}

	public static void setWindowId(int windowId) {
		NetLog.windowId = new Integer(windowId);
	}
}
