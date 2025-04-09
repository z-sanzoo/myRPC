package com.zishanshu.client;

import com.zishanshu.common.RPCRequest;
import com.zishanshu.common.RPCResponse;
import lombok.AllArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@AllArgsConstructor
public class ClientProxy implements InvocationHandler {
    String host;
    int port;

    // 这是一个动态代理类,让本地用户无感调用接口,其实实现在远程调用
    // Proxy.newProxyInstance 的参数分别为类加载器,接口数组和处理器, 这个生成的动态代理类可以看作是实现了接口数组的所有方法的一个类
    // 其中处理器是一个实现了 InvocationHandler 接口的对象,它的invoke方法会在代理对象调用接口方法时被调用
    // invoke 会在代理对象调用接口中定义的方法时被调用, 代理类会拦截所有方法的调用,并将调用转发到 invoke 方法中
    // getDeclaringClass() 方法返回一个 Class 对象,它表示声明了该方法的类
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String interfaceName = method.getDeclaringClass().getName();
        String methodName = method.getName();
        Class<?>[] paramTypes = method.getParameterTypes();

        // 1. 构建请求对象
        RPCRequest rpcRequest = RPCRequest.builder()
                .interfaceName(interfaceName)
                .methodName(methodName)
                .params(args)
                .paramTypes(paramTypes)
                .build();
//        System.out.println("构建请求对象");

        // 2. 发送请求
        RPCResponse response = IOClient.sendRequest(host, port, rpcRequest);
//        System.out.println("发送请求");

        // 3. 返回结果
        if (response.getCode() == 200) {
            return response.getData();
        } else {
            throw new RuntimeException("RPC调用失败");
        }
    }

    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
    }
}
