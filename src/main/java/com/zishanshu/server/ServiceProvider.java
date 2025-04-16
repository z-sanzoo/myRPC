package com.zishanshu.server;


import com.zishanshu.register.ServiceRegister;
import com.zishanshu.register.ZKServiceRegister;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ServiceProvider {
    private Map<String, Object> interfaceProvider;
    private final ServiceRegister register;
    private final InetSocketAddress ServiceAddress;
    public ServiceProvider(String host, int port) {
        this.interfaceProvider = new HashMap<>();
        register = new ZKServiceRegister();
        ServiceAddress = new InetSocketAddress(host, port);
    }

    public void provideServiceInterface(Object service){
        String serviceName = service.getClass().getName();
        Class<?>[] interfaces = service.getClass().getInterfaces();

        for(Class<?> clazz : interfaces){
            interfaceProvider.put(clazz.getName(),service);
            register.register(clazz.getName(), ServiceAddress,true);
        }

    }

    public Object getService(String interfaceName){
        return interfaceProvider.get(interfaceName);
    }

    public void close(){
        register.deregister();
    }
}
