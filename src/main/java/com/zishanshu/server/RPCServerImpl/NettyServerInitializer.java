package com.zishanshu.server.RPCServerImpl;

import com.zishanshu.common.Decoder;
import com.zishanshu.common.Encoder;
import com.zishanshu.common.SeriailizerImpl.FastjsonSerializer;
import com.zishanshu.common.SeriailizerImpl.ProtobufSerializer;
import com.zishanshu.server.ServiceProvider;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NettyServerInitializer extends ChannelInitializer<NioSocketChannel> {

    ServiceProvider serviceProvider;

    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
        ch.pipeline().addLast(new Decoder());
        ch.pipeline().addLast(new NettyRPCServerHandler(serviceProvider));
        ch.pipeline().addLast(new Encoder(new ProtobufSerializer()));
    }
}
