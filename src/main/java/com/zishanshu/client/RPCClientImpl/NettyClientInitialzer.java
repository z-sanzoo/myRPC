package com.zishanshu.client.RPCClientImpl;

import com.zishanshu.server.RPCServerImpl.NettyRPCServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class NettyClientInitialzer extends ChannelInitializer<NioSocketChannel> {
    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {

        // ch1 :服务端对于入站的request首先进行解码,格式是[长度][消息体],解决粘包问题
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,4,0,4));
        // ch2:服务端对于入站的request进行解码,将字节流转换为对象
        ch.pipeline().addLast(new ObjectDecoder(new ClassResolver() {
            @Override
            public Class<?> resolve(String className) throws ClassNotFoundException {
                return Class.forName(className);
            }
        }));
        // ch3 变成Request对象之后,交给NettyRPCServerHandler处理
        ch.pipeline().addLast(new NettyClientHandler());
        // 出站处理
        //ch5:服务端对于出站的 request 进行编码,格式是[长度][消息体],加上4位长度
        ch.pipeline().addLast(new LengthFieldPrepender(4));
        // ch4:客户端对于 request 进行编码
        ch.pipeline().addLast(new ObjectEncoder());

    }
}
