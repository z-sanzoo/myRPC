package com.zishanshu.server.RPCServerImpl;


import com.zishanshu.server.RPCServer;
import com.zishanshu.server.ServiceProvider;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyPRCServer implements RPCServer {


    ServiceProvider serviceProvider;


    public NettyPRCServer(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }


    @Override
    public void start(int port)  {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);//负责处理accept的线程数量设置为1用来处理连接,
        NioEventLoopGroup workGroup = new NioEventLoopGroup();//  处理数据的线程数量为cpu核数*2(默认的)
        try{
            ChannelFuture channelFuture = new ServerBootstrap().group(bossGroup,workGroup)
                                        .channel(NioServerSocketChannel.class)
                                        .childHandler(new NettyServerInitializer(serviceProvider))
                                        .bind(port)
                                        .sync();
            // 为什么netty要把channel的连接和关闭都设置为异步的方法?
            // 因为netty是一个异步的框架,它的设计就是为了处理高并发的场景,如果把连接和关闭都设置为同步的方法,那么就会阻塞住nio线程,导致性能下降
            // 所以netty是一个类似流水线的设计,多个线程执行相应的工作,尽管一个请求的处理流程是变长了,但是整个系统的吞吐量是提高了
            log.info( "Netty RPC服务器启动,端口:{}", port);
            // 调用了closeFuture之后,这个channel会一直阻塞,直到这个channel关闭
            channelFuture.channel().closeFuture().sync();
            //还可以在closeFuture上添加一个监听器,当这个channel关闭的时候,会调用这个监听器
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            // 关闭线程池
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            log.info("Netty RPC服务器关闭");
        }
    }

    @Override
    public void stop() {

    }
}
