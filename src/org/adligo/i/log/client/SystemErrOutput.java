package org.adligo.i.log.client;

public class SystemErrOutput implements I_LogOutput {

	public void write(String p) {
		System.err.println(p);
	}

}
