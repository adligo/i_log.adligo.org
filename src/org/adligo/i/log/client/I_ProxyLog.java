package org.adligo.i.log.client;

import org.adligo.i.util.client.I_Iterator;

public interface I_ProxyLog extends I_LogDelegate {
    
    public I_Iterator getDelegates();
    public void addDelegate(I_LogDelegate p);
    public String getLogName();
}
