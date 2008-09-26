package org.adligo.i.log.client;

import org.adligo.i.util.client.I_ImutableMap;
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
	
	public static final I_ImutableMap URL_ESCAPE_CODES = getUrlEscapeCodes();
	private boolean hasQ = false;
	
	private static I_ImutableMap getUrlEscapeCodes() {
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
