package org.adligo.i.log.client;

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
	
	private boolean hasQ = false;
	
	public LogUrl(String inital, boolean hasQ) {
		sb.append(inital);
		this.hasQ = hasQ;
	}
	
	public Object clone() {
		return new LogUrl(sb.toString(), hasQ);
	}
	public void append(String key, String value) {
		if (!hasQ) {
			sb.append("?");
			hasQ= true;
		} else {
			sb.append("&");
		}
		sb.append(key);
		sb.append("=");
		cleanAndAppend(value);
	}
	
	private void cleanAndAppend(String p) {
		char [] chars = p.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (c == ' ') {
				sb.append("%20");
			} else {
				sb.append(c);
			}
		}
	}
	
	public void append(String key, int value) {
		if (!hasQ) {
			sb.append("?");
			hasQ= true;
		} else {
			sb.append("&");
		}
		sb.append(key);
		sb.append("=");
		sb.append(value);
	}
	
	public String toString(){
		return sb.toString();
	}
}
