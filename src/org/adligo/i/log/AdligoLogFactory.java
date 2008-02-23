package org.adligo.i.log;

import java.util.Hashtable;

/**
 * a ultra simple log factory for CLDC 2.0 usage
 * in cell phones exc 
 * 
 * @author scott
 *
 */
public class AdligoLogFactory implements I_LogFactory {
	volatile private Hashtable loggers = new Hashtable();
	
	public Log getLog(Class clazz) {
		//System.out.println("getting log for " + clazz);
		Log toRet = (Log) loggers.get(clazz);
		if (toRet == null) {
			synchronized (this) {
				toRet = new SimpleLog(clazz.getName());
				loggers.put(clazz, toRet);
			}
		}
		return toRet;
	}

}
