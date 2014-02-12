package org.adligo.i.log.shared;

import org.adligo.i.util.shared.I_Iterator;

public interface I_ProxyLog extends I_LogDelegate {
    
    public I_Iterator getDelegates();
    public void addDelegate(I_LogDelegate p);
    public String getLogName();
}
