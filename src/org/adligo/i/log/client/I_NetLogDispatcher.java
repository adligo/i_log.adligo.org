package org.adligo.i.log.client;

/**
 * note that the actual implementaion may 
 * batch up requests into a single 
 * @author scott
 *
 */
public interface I_NetLogDispatcher {
	public void dispatch(LogUrl url);
}
