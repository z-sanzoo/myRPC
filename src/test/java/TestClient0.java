import com.zishanshu.client.ClientProxyImpl.ClientProxy;
import com.zishanshu.client.RPCClientImpl.NettyRPCClient;
import com.zishanshu.client.RPCClient;
import com.zishanshu.service.BlogService;
import org.junit.Test;

public class TestClient0 {
    public static void main(String[] args) throws Exception {
        ClientProxy clientProxy = new ClientProxy();
        BlogService blogService = clientProxy.getProxy(BlogService.class);
        int count = 0;
        while(true){
            Thread.sleep(2000);
            System.out.println(blogService.getBlogById(count++));
        }
    }

}
