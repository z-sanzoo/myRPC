package com.zishanshu.register;

import java.net.InetSocketAddress;

public interface ServiceRegister {
    void register(String serviceName, InetSocketAddress address, boolean canRetry);
    void deregister();
    boolean checkRetry(String serviceName) ;
    InetSocketAddress serviceDiscovery(String serviceName);

}
