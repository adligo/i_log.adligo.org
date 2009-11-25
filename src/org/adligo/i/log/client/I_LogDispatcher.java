package org.adligo.i.log.client;

import org.adligo.i.log.client.models.I_LogMessage;
import org.adligo.i.log.client.models.LogMessage;

/**
 * note that the actual impl. may 
 * batch up requests for remote send 
 * 
 * @author scott
 *
 */
public interface I_LogDispatcher {
	public void dispatch(I_LogMessage message);
}
