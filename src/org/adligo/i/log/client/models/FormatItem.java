package org.adligo.i.log.client.models;

import org.adligo.i.util.client.AppenderFactory;
import org.adligo.i.util.client.I_Appender;

public class FormatItem {
	/**
	 * try to match log4j the original 
	 * http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/PatternLayout.html
	 */
	public static final char LEAD_CHAR = '%';
	public static final char PRIORITY = 'p';
	public static final char THREAD = 't';
	public static final char MESSAGE = 'm';
	public static final char CATEGORY = 'c';
	public static final char BRACKET_START = '{';
	public static final char BRACKET_END = '}';
	
	public static final short STRING_TYPE = 0;
	public static final short PRIORITY_TYPE = 1;
	public static final short THREAD_TYPE = 2;
	public static final short MESSAGE_TYPE = 3;
	public static final short CATEGORY_TYPE = 4;
	
	/**
	 * one of the above STRING_TYPE for example
	 */
	private short type;
	private String asString;
	private short category_precision;
	
	public FormatItem(short p_type) {
		this(p_type, null);
	}
	
	public FormatItem(short p_type, String p_string) {
		type = p_type;
		asString = p_string;
	}
	
	public short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public String getAsString() {
		return asString;
	}

	public void setAsString(String asString) {
		this.asString = asString;
	}
	
	public short getCategory_precision() {
		return category_precision;
	}

	public void setCategory_precision(short categoryPrecision) {
		category_precision = categoryPrecision;
	}

	public String toString() {
		I_Appender sb = AppenderFactory.create();
		sb.append("FormatItem [type=");
		sb.append(type);
		sb.append(",string=");
		sb.append(asString);
		sb.append(",category_precision=");
		sb.append(category_precision);
		return sb.toString();
	}
}
