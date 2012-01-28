package org.adligo.i.log.client;

import org.adligo.i.util.client.I_StringAppender;

/**
 * to keep backward compatibility with jme
 * @author scott
 *
 */
public class StringBufferAppender implements I_StringAppender {
	private StringBuffer sb;
	
	public StringBufferAppender(StringBuffer p) {
		sb = p;
	}
	public void append(String p) {
		sb.append(p);
	}

}
