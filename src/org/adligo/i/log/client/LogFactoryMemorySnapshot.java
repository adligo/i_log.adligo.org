package org.adligo.i.log.client;

import org.adligo.i.util.client.I_HashCollection;
import org.adligo.i.util.client.I_Map;

public class LogFactoryMemorySnapshot {
	private LogFactoryMemory memory;
	
	public LogFactoryMemorySnapshot(LogFactoryMemory p) {
		memory = new LogFactoryMemory(p);
	}
	
	public I_HashCollection getPreInitLoggers() {
		return memory.preInitLoggers;
	}
	
	public I_Map getLoggers() {
		return memory.loggers;
	}
	
	public boolean isFirstCallToSetInitalLogLevels() {
		return memory.firstCallToSetInitalLogLevels;
	}
}
