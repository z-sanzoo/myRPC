package com.zishanshu.client;

import com.zishanshu.common.RPCRequest;
import com.zishanshu.common.RPCResponse;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class IOClient {
    public static RPCResponse sendRequest(String host, int port, RPCRequest rpcRequest) {

        try (Socket socket = new Socket(host, port)){
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
//            System.out.println("连接成功");

            oos.writeObject(rpcRequest);
            oos.flush();
//            System.out.println("发送请求成功");

            return (RPCResponse) ois.readObject();
        } catch (IOException | ClassNotFoundException e ) {
            e.printStackTrace();
            System.out.println("客户端启动失败");
            return null;
        }


    }
}
