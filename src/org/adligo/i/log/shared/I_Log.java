package org.adligo.i.log.shared;

public interface I_Log {
	/**
	 * returns the name of the log or log Class
	 * is used to determine a unique logger
	 * equals and hashCode
	 * 
	 * @return
	 */
	public String getLogName();
}
