package com.zishanshu.register;

import java.net.InetSocketAddress;

public interface ServiceRegister {
    void register(String serviceName, InetSocketAddress address);
    InetSocketAddress serviceDiscovery(String serviceName);
}
