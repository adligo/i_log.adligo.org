package org.adligo.i.log.shared;

import org.adligo.i.log.shared.models.I_LogMessage;

public interface I_Formatter {
	
	public String getFormatString();
	
	public void setFormatString(String format);
	/**
	 * impls should be thread safe
	 * @param message
	 * @return
	 */
	public String format(I_LogMessage message);
}
