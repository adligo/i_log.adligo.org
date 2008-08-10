package org.adligo.i.log.client;

import org.adligo.i.util.client.ClassUtils;
import org.adligo.i.util.client.CollectionFactory;
import org.adligo.i.util.client.Event;
import org.adligo.i.util.client.I_Collection;
import org.adligo.i.util.client.I_Listener;
import org.adligo.i.util.client.I_Map;
import org.adligo.i.util.client.MapFactory;

/**
 * a ultra simple log factory for CLDC 2.0 usage
 * in cell phones and GWT 
 * exc 
 * 
 * @author scott
 *
 */
public class AdligoLogFactory implements I_LogFactory, I_LogFactoryContainer {


	volatile private I_Map loggers = MapFactory.create();
	
	public Log getLog(Class clazz) {
		//System.out.println("getting log for " + clazz);
		Log toRet = (Log) loggers.get(clazz);
		if (toRet == null) {
			
			synchronized (this) {
				String name = ClassUtils.getClassName(clazz);
				if (LogPlatform.isLogEnabled()) {
					LogPlatform.log("getting log for " + name);
				}
				toRet = new SimpleLog(name, LogPlatform.getProps());
				loggers.put(clazz, toRet);
			}
		}
		return toRet;
	}

	
	
	public I_LogFactory getLogFactory() {
		return this;
	}
}
