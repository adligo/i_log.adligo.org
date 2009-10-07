package org.adligo.i.log.client.models;

public interface I_LogMessage {
	public short getLevel();
	public Throwable getThrowable();
	public String getName();
	public String getLevelString();
	public Integer getWindowId();
	public Object getMessage();
	/**
	 * note is set up by the I_ThreadPopulator set in the 
	 * LogPlatform
	 * 
	 * @return
	 */
	public String getThread();
}
