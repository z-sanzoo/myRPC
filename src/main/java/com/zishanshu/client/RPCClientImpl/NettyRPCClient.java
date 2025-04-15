package com.zishanshu.client.RPCClientImpl;

import com.alibaba.fastjson.parser.ParserConfig;
import com.zishanshu.client.RPCClient;
import com.zishanshu.common.RPCRequest;
import com.zishanshu.common.RPCResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
@Slf4j
public class NettyRPCClient implements RPCClient {
    private final String host;
    private final int port;
    private static final Bootstrap bootstrap;
    private static final EventLoopGroup group;
    static {
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new NettyClientInitialzer());
    }

    public NettyRPCClient(String host, int port) {
        this.host = host;
        this.port = port;
    }



    @Override
    public RPCResponse sendRequest(RPCRequest rpcRequest) {
        try {
            Channel channel = bootstrap.connect(this.host,this.port).sync().channel();
            // 发送结果
            channel.writeAndFlush(rpcRequest);
            // 阻塞住`
            channel.closeFuture().sync();


            AttributeKey<RPCResponse> key = AttributeKey.valueOf("RPCResponse");
            RPCResponse response = channel.attr(key).get();


            log.debug("收到response"+ response);
            return response;

        } catch ( InterruptedException e) {
            log.debug("发送异常");
            return null;
        }
    }
}