package org.adligo.i.log.client.models;

import org.adligo.i.log.client.DeferredLog;
import org.adligo.i.util.client.I_ImmutableMap;
import org.adligo.i.util.client.I_Map;
import org.adligo.i.util.client.MapFactory;

/**
 * this class is for sending a log message over http
 * for instance from the browser in GWT
 * or from a phone
 * or remote jvm for stats exc
 * 
 * @author scott
 *
 */
public class LogUrl {
	StringBuffer sb = new StringBuffer();
	/**
	 * the key to a value in the log url
	 * which corresponds to the integers in SimpleLog like LOG_LEVEL_DEBUG
	 */
	public static final String LEVEL = "l";
	/**
	 * the key to the message
	 */
	public static final String MESSAGE = "m";
	/**
	 * the key to the stack trace 
	 */
	public static final String STACK = "s";
	/**
	 * the key to the thread
	 */
	public static final String THREAD = "t";
	/**
	 * the key to the time 
	 */
	public static final String TIME = "ms";
	/**
	 * the name of the Log instance
	 */
	public static final String NAME = "n";
	/**
	 * the id of the window (kept in session)
	 * of the browser window sending this log message
	 */
	public static final String WINDOW_ID = "w";
	/**
	 * for non cookie users
	 * SET by the Dispatcher!
	 */
	public static final String JSESSION_ID = "jsessionid";
	/**
	 * request id
	 * SET by the Dispatcher!
	 */
	public static final String REQUEST_ID = "r";
	
	public static final I_ImmutableMap URL_ESCAPE_CODES = getUrlEscapeCodes();
	private boolean hasQ = false;
	private boolean firstParam = true;
	/**
	 * booleans to track if certain cgi parameters have been
	 * added, this is to Batch Log Messages in order to keep the
	 * Request Number down on the Google app engine which is limited to 
	 * 1,333,328 for free users even though the 
	 * datastore and memapi will take 10x as many calls
	 * 
	 * This is so that the messages will not be mismatched for a url like;
	 * ?l=1&m=hey&n=foo&l=2&m=dude&l=3&n=bar&m=bar_will_show_up_with_dude
	 * 
	 */
	private boolean hasLevel = false;
	private boolean hasMessage = false;
	private boolean hasStack = false;
	private boolean hasThread = false;
	private boolean hasTime = false;
	private boolean hasName = false;
	private boolean hasWindowId = false;
	// Note JSession Id and RequestId are only sent once, and should be added by the I_NetLogDispatcher
	
	private static I_ImmutableMap getUrlEscapeCodes() {
		I_Map toRet = MapFactory.create();
		//copied from http://www.december.com/html/spec/esccodes.html
		toRet.put(new Character(' '), new String("%20"));
		toRet.put(new Character('<'), new String("%3C"));
		toRet.put(new Character('>'), new String("%3E"));
		toRet.put(new Character('#'), new String("%23"));
		toRet.put(new Character('%'), new String("%25"));
		toRet.put(new Character('{'), new String("%7B"));
		toRet.put(new Character('}'), new String("%7D"));
		toRet.put(new Character('|'), new String("%7C"));
		toRet.put(new Character('\\'), new String("%5C"));
		toRet.put(new Character('^'), new String("%5E"));
		toRet.put(new Character('~'), new String("%7E"));
		
		toRet.put(new Character('['), new String("%5B"));
		toRet.put(new Character(']'), new String("%5D"));
		toRet.put(new Character('`'), new String("%60"));
		toRet.put(new Character(';'), new String("%3B"));
		toRet.put(new Character('/'), new String("%2F"));
		toRet.put(new Character('?'), new String("%3F"));
		toRet.put(new Character(':'), new String("%3A"));
		toRet.put(new Character('@'), new String("%40"));
		toRet.put(new Character('='), new String("%3D"));
		toRet.put(new Character('&'), new String("%26"));
		toRet.put(new Character('$'), new String("%24"));
		
		
		
		return MapFactory.create(toRet);
	}
	public LogUrl(String inital, boolean hasQ) {
		sb.append(inital);
		this.hasQ = hasQ;
	}
	
	public LogUrl(String inital, boolean hasQ, LogMessage message) {
		sb.append(inital);
		this.hasQ = hasQ;
		appendMessage(message);
	}
	
	public LogUrl(LogMessage message) {
		this.hasQ = false;
		appendMessage(message);
	}
	
	public void appendMessage(LogMessage message) {
		StringLogMessage m = new StringLogMessage(message);
		this.append(LEVEL, m.getLevel());
		this.append(NAME, m.getName());
		this.append(WINDOW_ID, "" +m.getWindowId());
		this.append(MESSAGE, "" +m.getMessageWithStack());
	}
	
	public Object clone() {
		return new LogUrl(sb.toString(), hasQ);
	}
	public void append(String key, String value) {
		appendQandAnd();
		trackKey(key);
		sb.append(key);
		sb.append("=");
		cleanAndAppend(value);
	}
	private void appendQandAnd() {
		if (!hasQ) {
			sb.append("?");
			hasQ= true;
			firstParam = false;
		} else {
			if (!firstParam) {
				sb.append("&");
			} else {
				firstParam = false;
			}
		}
	}
	
	private void cleanAndAppend(String p) {
		char [] chars = p.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			Character c = new Character(chars[i]);
			String s = (String) URL_ESCAPE_CODES.get((c));
			if (s != null) {
				sb.append(s);
			} else {
				sb.append(c);
			} 
		}
	}
	
	public void append(String key, int value) {
		appendQandAnd();
		trackKey(key);
		sb.append(key);
		sb.append("=");
		sb.append(value);
	}
	
	void trackKey(String key) {
		if (LEVEL.equals(key)) {
			hasLevel = true;
		} else if (MESSAGE.equals(key)) {
			hasMessage = true;
		} else if (STACK.equals(key)) {
			hasStack = true;
		} else if (THREAD.equals(key)) {
			hasThread = true;
		} else if (TIME.equals(key)) {
			hasTime = true;
		} else if (NAME.equals(key)) {
			hasName = true;
		} else if (WINDOW_ID.equals(key)) {
			hasWindowId = true;
		}
	}
	public boolean isHasQ() {
		return hasQ;
	}
	public boolean isHasLevel() {
		return hasLevel;
	}
	public boolean isHasMessage() {
		return hasMessage;
	}
	public boolean isHasStack() {
		return hasStack;
	}
	public boolean isHasThread() {
		return hasThread;
	}
	public boolean isHasTime() {
		return hasTime;
	}
	public boolean isHasName() {
		return hasName;
	}
	public boolean isHasWindowId() {
		return hasWindowId;
	}
	public void addMissingParameters() {
		if (!hasLevel) {
			append(LEVEL, DeferredLog.LOG_LEVEL_ALL);
		}
		if (!hasMessage) {
			append(MESSAGE, "");
		}
		if (!hasStack) {
			append(STACK, "");
		}
		if (!hasThread) {
			append(THREAD, "");
		}
		if (!hasName) {
			append(NAME, "");
		}
		if (!hasWindowId) {
			append(WINDOW_ID, "");
		}
	}
	public String toString(){
		return sb.toString();
	}
	
	public String toQueryString(){
		String url = sb.toString();
		if (hasQ) {
			int start = url.indexOf("?") + 1;
			if (url.length() > start) {
				url = url.substring(start, url.length());
			} else {
				url = "";
			}
		}
		return url;
	}
}
