import com.zishanshu.server.RPCServer;
import com.zishanshu.server.RPCServerImpl.NettyPRCServer;
import com.zishanshu.server.ServiceProvider;
import com.zishanshu.service.Impl.BlogServiceImpl;
import com.zishanshu.service.Impl.UserServiceImpl;

public class TestServer {
    public static void main(String[] args) {
//         创建一个服务提供者
        final int port = 8899;

        ServiceProvider serviceProvider = new ServiceProvider("localhost", port);

        serviceProvider.provideServiceInterface(new BlogServiceImpl());
        serviceProvider.provideServiceInterface(new UserServiceImpl());


        // 创建一个线程池RPC服务器
        RPCServer server = new NettyPRCServer(serviceProvider);
         // 启动服务器
        server.start(port);
    }

}
