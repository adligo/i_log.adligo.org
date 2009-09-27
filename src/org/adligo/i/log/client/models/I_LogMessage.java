package org.adligo.i.log.client.models;

public interface I_LogMessage {
	public short getLevel();
	public Throwable getThrowable();
	public String getName();
	public String getLevelString();
	public Integer getWindowId();
	public Object getMessage();
	/**
	 * note should be set in the dispatcher
	 * @return
	 */
	public String getThread();
}
