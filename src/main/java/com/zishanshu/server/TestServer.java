package com.zishanshu.server;

import com.zishanshu.server.Impl.ThreadPoolRPCServer;
import com.zishanshu.service.Impl.BlogServiceImpl;
import com.zishanshu.service.Impl.UserServiceImpl;

import java.util.HashMap;
import java.util.Map;

public class TestServer {
    public static void main(String[] args) {
//         创建一个服务提供者


        ServiceProvider serviceProvider = new ServiceProvider();

        serviceProvider.provideServiceInterface(new BlogServiceImpl());
        serviceProvider.provideServiceInterface(new UserServiceImpl());


        // 创建一个线程池RPC服务器
         ThreadPoolRPCServer server = new ThreadPoolRPCServer(serviceProvider);
         // 启动服务器
         server.start(8080);
    }

}
