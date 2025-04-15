package com.zishanshu.client.RPCClientImpl;

import com.zishanshu.common.Decoder;
import com.zishanshu.common.Encoder;
import com.zishanshu.common.SeriailizerImpl.FastjsonSerializer;
import com.zishanshu.common.SeriailizerImpl.ProtobufSerializer;
import com.zishanshu.server.RPCServerImpl.NettyRPCServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyClientInitialzer extends ChannelInitializer<NioSocketChannel> {
    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
        ch.pipeline().addLast(new Decoder());

        ch.pipeline().addLast(new NettyClientHandler());
        ch.pipeline().addLast(new Encoder(new ProtobufSerializer()));

    }
}
