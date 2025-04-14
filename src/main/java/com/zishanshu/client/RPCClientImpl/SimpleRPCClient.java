package com.zishanshu.client.RPCClientImpl;

import com.zishanshu.client.RPCClient;
import com.zishanshu.common.RPCRequest;
import com.zishanshu.common.RPCResponse;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


@AllArgsConstructor
public class SimpleRPCClient implements RPCClient {
    private String host;
    private int port;

    @Override
    public RPCResponse sendRequest(RPCRequest rpcRequest) {
        try (Socket socket = new Socket(host, port)){
            // 发起一次Socket连接请求
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();

            return (RPCResponse) objectInputStream.readObject();

        } catch (  ClassNotFoundException | IOException e) {
            System.out.println();
            return null;
        }
    }
}
