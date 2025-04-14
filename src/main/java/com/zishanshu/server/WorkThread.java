package com.zishanshu.server;


import com.zishanshu.common.RPCRequest;
import com.zishanshu.common.RPCResponse;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;


@AllArgsConstructor
public class WorkThread implements Runnable{
    private Socket socket;
    private ServiceProvider serviceProvider;


    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            RPCRequest rpcRequest = (RPCRequest) in.readObject();
            RPCResponse rpcResponse = getResponse(rpcRequest);
            out.writeObject(rpcResponse);
            out.flush();
        } catch (ClassNotFoundException e) {
            System.out.println("IO异常");
            throw new RuntimeException(e);

        } catch (IOException e) {
            System.out.println("其他异常");
            throw new RuntimeException(e);
        }
    }

    private RPCResponse getResponse(RPCRequest request) {
        String interfaceName = request.getInterfaceName();
        Object service = serviceProvider.getService(interfaceName);
        if (service == null) {
            System.out.println("没有找到对应的服务");
            return RPCResponse.fail();
        }
        try {
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParamTypes());
            Object res = method.invoke(service, request.getParams());
            return RPCResponse.success(res);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.out.println("方法执行错误");
            return RPCResponse.fail();
        }
    }

}