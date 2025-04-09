package com.zishanshu.server.Impl;

import com.zishanshu.server.RPCServer;
import com.zishanshu.server.ServiceProvider;
import com.zishanshu.server.WorkThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolRPCServer implements RPCServer {
    ServiceProvider serviceProvider;
    public final ThreadPoolExecutor threadPool;

    public ThreadPoolRPCServer(ServiceProvider serviceProvider){
        threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                1000, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));
        this.serviceProvider = serviceProvider;
    }

    public  ThreadPoolRPCServer(ServiceProvider serviceProvider, int corePoolSize,
                                       int maximumPoolSize,
                                       long keepAliveTime,
                                       TimeUnit unit,
                                       BlockingQueue<Runnable> workQueue){

        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        this.serviceProvider = serviceProvider;
    }

    @Override
    public void start(int port) {
        System.out.println("服务端启动了");
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("有新的连接");
                threadPool.execute(new WorkThread(socket, serviceProvider));
            }
        } catch (IOException e) {
            System.out.println("IO异常");
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {

    }
}
