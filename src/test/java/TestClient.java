import com.zishanshu.client.ClientProxyImpl.ClientProxy;
import com.zishanshu.client.RPCClientImpl.NettyRPCClient;
import com.zishanshu.client.RPCClientImpl.SimpleRPCClient;
import com.zishanshu.client.RPCClient;
import com.zishanshu.domain.User;
import com.zishanshu.service.BlogService;
import com.zishanshu.service.UserService;

public class TestClient {
    public static void main(String[] args) {
        // 构建一个使用java Socket传输的客户端
        RPCClient RPCClient = new NettyRPCClient("127.0.0.1", 8080);
// 把这个客户端传入代理客户端
        ClientProxy clientProxy = new ClientProxy(RPCClient);
// 代理客户端根据不同的服务，获得一个代理类， 并且这个代理类的方法以或者增强（封装数据，发送请求）
        UserService userService = clientProxy.getProxy(UserService.class);
// 调用方法
        User userByUserId = userService.getUserById(10);
        System.out.println(userByUserId);

//
//        BlogService blogService = clientProxy.getProxy(BlogService.class);
//
//        System.out.println(blogService.getBlogById(10));


    }
}
