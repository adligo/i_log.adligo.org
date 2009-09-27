package org.adligo.i.log.client;

import org.adligo.i.log.client.models.I_LogMessage;
import org.adligo.i.log.client.models.LogMessage;

/**
 * note that the actual implementaion may 
 * batch up requests into a single 
 * @author scott
 *
 */
public interface I_LogDispatcher {
	public void dispatch(I_LogMessage message);
}
