package org.adligo.i.log.shared;

import org.adligo.i.util.shared.I_HashCollection;
import org.adligo.i.util.shared.I_Map;

public class LogFactoryMemorySnapshot {
	private LogFactoryMemory memory;
	
	public LogFactoryMemorySnapshot(LogFactoryMemory p) {
		memory = new LogFactoryMemory(p);
	}
	
	public I_HashCollection getPreInitLoggers() {
		return memory.getPreInitLoggers();
	}
	
	public I_Map getLoggers() {
		return memory.getLoggers();
	}
	
	public boolean isFirstCallToSetInitalLogLevels() {
		return memory.isfirstCallToSetInitalLogLevels();
	}
}
