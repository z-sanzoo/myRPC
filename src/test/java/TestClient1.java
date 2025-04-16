import com.zishanshu.client.ClientProxyImpl.ClientProxy;
import com.zishanshu.client.RPCClient;
import com.zishanshu.client.RPCClientImpl.NettyRPCClient;
import com.zishanshu.service.BlogService;
import com.zishanshu.service.UserService;

public class TestClient1 {
    public static void main(String[] args) throws Exception {
        ClientProxy clientProxy = new ClientProxy();
        BlogService blogService = clientProxy.getProxy(BlogService.class);
        UserService userService = clientProxy.getProxy(UserService.class);
        int count = 0;
        while(true){
            Thread.sleep(2000);
            System.out.println(userService.getUserById(count));
            System.out.println(blogService.getBlogById(count++));
        }
    }

}
