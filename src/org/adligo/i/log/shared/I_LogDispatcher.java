package org.adligo.i.log.shared;

import org.adligo.i.log.shared.models.I_LogMessage;

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
