package org.adligo.i.log.client;

public class SystemErrOutput implements I_LogOutput {

	public void write(String p) {
		System.err.println(p);
		if (LogPlatform.isTrace()) {
			Exception trace = new Exception("Called System.err.println");
			trace.fillInStackTrace();
			LogPlatform.trace("SystemErrOutput",trace);
		}
	}

}
