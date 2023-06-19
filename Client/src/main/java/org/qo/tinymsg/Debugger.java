package org.qo.tinymsg;

import java.net.InetAddress;

public class Debugger {
    public final boolean testConnection(String url) throws Exception{
        int  timeOut =  3000 ;
        if (InetAddress.getByName(url).isReachable(timeOut)) {
            return false;
        }
        return true;
    }
}
