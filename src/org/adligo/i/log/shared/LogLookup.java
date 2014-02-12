package org.adligo.i.log.shared;

public class LogLookup implements I_Log {
	private String logName = "";

	public LogLookup(String name) {
		if (name == null) {
			return;
		}
		logName = name;
	}
	
	public String getLogName() {
		return logName;
	}

	public int hashCode() {
		return logName.hashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() == obj.getClass()) {
			return equals2((I_Log) obj);
		}
		try {
			return equals2((I_Log) obj);
		} catch (ClassCastException x) {
			//do nothing;
		}
		return false;
	}

	private boolean equals2(I_Log obj) {
		if (logName == null) {
			if (obj.getLogName() != null)
				return false;
		} else if (!logName.equals(obj.getLogName()))
			return false;
		return true;
	}
	
}
