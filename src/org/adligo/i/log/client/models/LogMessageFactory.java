package org.adligo.i.log.client.models;

import org.adligo.i.util.client.ClassUtils;

public class LogMessageFactory {

	public static LogMessage createMessage(Object message) {
		LogMessage toRet;
		if (message == null) {
			toRet = new StringLogMessage();
			toRet.setMessage("");
		} else if (ClassUtils.typeOf(message, String.class)) {
			toRet = new StringLogMessage();
			toRet.setMessage(message);
		} else {
			toRet = new ObjectLogMessage();
			toRet.setMessage(message);
		}		
		return toRet;
	}
	
	private LogMessageFactory () {}
}
