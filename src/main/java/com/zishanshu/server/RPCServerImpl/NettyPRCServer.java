package com.zishanshu.server.RPCServerImpl;


import com.zishanshu.server.RPCServer;
import com.zishanshu.server.ServiceProvider;
import com.zishanshu.service.Impl.UserServiceImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyPRCServer implements RPCServer {

    ServiceProvider serviceProvider;

    Channel channel = null;

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
//            log.info( "Netty RPC服务器启动,端口:{}", port);
            // 调用了closeFuture的sync之后,这个channel会一直阻塞,直到这个channel关闭

            channel = channelFuture.channel();

//            当调用channel.close()方法主动关闭Channel时，Netty会完成以下流程：
//            停止接受新连接
//                    等待现有数据传输完成
//            释放底层Socket资源
//            将closeFuture标记为完成状态，触发注册的监听器12
            // 注册回调方法
            channel.closeFuture().addListener((ChannelFutureListener) future -> {
                workGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
                log.info("Netty RPC服务器关闭");
            });

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        // 首先让删除zookeeper本服务节点,从而让zookeeper通知所有订阅该服务的客户端做出调整
        serviceProvider.close();
        try {
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        // 然后关闭netty的channel
        if (channel != null) {
            channel.close();
        }
    }
}
