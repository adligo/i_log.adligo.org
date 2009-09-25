package org.adligo.i.log.client.models;

import java.io.Serializable;

import org.adligo.i.util.client.ClassUtils;

/**
 *  this is so I can use rpc for log messages if im running in gwt
 *  however Serializable isn't in j2me so gwt_util will need to wrap
 *  
 * @author scott
 *
 */
public class StringLogMessage extends LogMessage {
	private String message;

	public StringLogMessage(LogMessage p) {
		if (ClassUtils.typeOf(p, ObjectLogMessage.class)) {
			Object om = p.getMessage();
			if (om != null) {
				this.message = om.toString();
			}
		} else {
			this.message = ((StringLogMessage) p).getLogMessage();
		}
		super.setLevel(p.getLevel());
		super.setThrowable(p.getThrowable());
		super.setName(p.getName());
		super.setWindowId(p.getWindowId());
	}
	
	public StringLogMessage() {
		
	}

	public Object getMessage() {
		return message;
	}
	public void setMessage(Object message) {
		if (message != null) {
			this.message = message.toString();
		}
	}
	
	public String getLogMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessageWithStack() {
		super.fillInStack();
		return message + super.stackAsString;
	}
}
