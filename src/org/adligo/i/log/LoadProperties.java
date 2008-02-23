package org.adligo.i.log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

public class LoadProperties {
	public static final char EQUALS = '=';
	public static final char UNIX_LINE_FEED = (char) "\n".getBytes()[0];
	public static final char DOS_LINE_FEED = (char) "\r".getBytes()[0];
	private static final boolean SELF_LOG = false;
	private static Hashtable tables = new Hashtable();
	
	public static Hashtable loadProperties(String resourceName, boolean cache) throws IOException {
		Hashtable table = (Hashtable) tables.get(resourceName);
		if (table != null) {
			return table;
		}
		table = new Hashtable();
		
		 // Add props from the resource simplelog.properties
		InputStream in = null;
		in = Class.class.getResourceAsStream(resourceName);
		
		
        	
        //CLDC 2.0 doesn't have a StringBuilder,
    	// or properties, assue adligo_log.properties
    	// will only have askii characters (java class names, DEBUG exc)
    	StringBuffer key = new StringBuffer();
    	StringBuffer val = new StringBuffer();
    	boolean inKey = true;
    	byte [] bytes = new byte[1];
    	
    	while (in.read(bytes) != -1) {
    		char c  = (char) bytes[0];
    		if (SELF_LOG) {
    			System.out.println("ck char '" + c + "'");
    		}
    		if (EQUALS == c) {
    			inKey = false;
    		} else {
        		if (UNIX_LINE_FEED == c || DOS_LINE_FEED == c) {
        			if (SELF_LOG) {
            			System.out.println("putting '" + key.toString() + "','" +
            					val.toString() + "'");
        			}
        			table.put(key.toString(), val.toString());
        			inKey = true;
        			key = new StringBuffer();
                	val = new StringBuffer();
        		} else {
        			if (inKey) {
        				key.append(c);
        			} else {
        				val.append(c);
        			}
        		}
    		}
    	}
    	if (SELF_LOG) {
			System.out.println("putting '" + key.toString() + "','" +
					val.toString() + "'");
		}
		table.put(key.toString(), val.toString());
        in.close();
        if (cache) {
        	tables.put(resourceName, table);
        }
        return table;
	}
}
