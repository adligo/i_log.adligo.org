package org.adligo.i.log.shared.models;

public class ObjectLogMessage extends LogMessage {
	private Object message;

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	protected ObjectLogMessage() {
		
	}
	
	
}
