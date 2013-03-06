package org.adligo.i.log.client;

import org.adligo.i.util.client.ArrayCollection;
import org.adligo.i.util.client.HashCollection;
import org.adligo.i.util.client.I_Iterator;
import org.adligo.i.util.client.I_Map;
import org.adligo.i.util.client.MapFactory;

public class LogFactoryMemory {
	/**
	 * note due to initalization issues (chicken vs egg) with the MapFactory
	 * this is a Collection that I wrote
	 */
	volatile HashCollection preInitLoggers = new HashCollection();
	volatile I_Map loggers;
	volatile boolean firstCallToSetInitalLogLevels = true;
	
	void firstCallToSetInitalLogLevelsFinished() {
		firstCallToSetInitalLogLevels = false;
	}
	
	boolean isfirstCallToSetInitalLogLevels() {
		return firstCallToSetInitalLogLevels;
	}
	
	void addLogger(String name, I_LogDelegate log) {
		loggers.put(name, log);
	}
	
	I_LogDelegate getLogger(String name) {
		return (I_LogDelegate) loggers.get(name);
	}
	
	synchronized boolean isInit() {
		if (loggers == null) {
			return false;
		}
		return true;
	}
	
	synchronized void init() {
		loggers = MapFactory.create();
	}
	
	void addDeferredLog(DeferredLog log) {
		preInitLoggers.put(log);
	}
	
	DeferredLog getDeferredLog(String name) {
		return (DeferredLog) preInitLoggers.get(new LogLookup(name));
	}
	
	I_Iterator getDeferredLogs() {
		return preInitLoggers.getIterator();
	}
	
	ArrayCollection getDeferredLogsCollection() {
		ArrayCollection toRet = new ArrayCollection();
		I_Iterator it = preInitLoggers.getIterator();
		while (it.hasNext()) {
			toRet.add(it.next());
		}
		return toRet;
	}
	
	void clearDeferredLogs() {
		preInitLoggers.clear();
	}
	
	I_Iterator getLogs() {
		return loggers.getValuesIterator();
	}
}
