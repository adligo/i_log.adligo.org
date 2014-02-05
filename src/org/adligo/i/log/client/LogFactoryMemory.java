package org.adligo.i.log.client;

import org.adligo.i.util.client.ArrayCollection;
import org.adligo.i.util.client.HashCollection;
import org.adligo.i.util.client.I_Iterator;
import org.adligo.i.util.client.I_Map;
import org.adligo.i.util.client.MapFactory;
import org.adligo.i.util.client.SynchronizedHashCollection;

public class LogFactoryMemory {
	/**
	 * preInitLoggers are loggers that were created before the 
	 * log platform was initialized and will be kept because
	 * they have importance someone may have called log before the LogPlatform is initialized.
	 * 
	 * note due to initialization issues (chicken vs egg) with the MapFactory
	 * this is a Collection that I wrote
	 */
	 volatile SynchronizedHashCollection preInitLoggers = new SynchronizedHashCollection(new HashCollection());
	 /**
	  * the loggers known by the system
	  * may swap out if someone reloads the logging configuration
	  * at runtime (ie your servers buggy so lets just turn on the logging
	  * without rebooting it).
	  */
	 volatile I_Map loggers;
	 /**
	  * if this is the first initialization of the levels
	  * important because the log messages that were sent 
	  * before the log platform was initialized should be flushed
	  */
	 volatile boolean firstCallToSetInitalLogLevels = true;
	
	 public LogFactoryMemory() {}
	 
	public LogFactoryMemory(LogFactoryMemory other) {
		HashCollection copy = new HashCollection();
		I_Iterator it =  preInitLoggers.getIterator();
		while (it.hasNext()) {
			copy.add(it.next());
		}
		preInitLoggers = new SynchronizedHashCollection(copy);
		I_Map copyMap = MapFactory.create();
		I_Iterator keys = loggers.getKeysIterator();
		I_Iterator values = loggers.getKeysIterator();
		while (keys.hasNext()) {
			copyMap.put(keys.next(), values.next());
		}
		
		firstCallToSetInitalLogLevels = other.firstCallToSetInitalLogLevels;
	}
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
	
	public LogFactoryMemorySnapshot getSnapshot() {
		return new LogFactoryMemorySnapshot(new LogFactoryMemory(this));
	}
}
