package com.zishanshu.register;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.CuratorConnectionLossException;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class ZKServiceRegister implements ServiceRegister {
    private static final String ROOT_PATH = "myRPC";
    private CuratorFramework client;
    public ZKServiceRegister() {
        // 指数事件重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

        // zookeeper的地址是固定的
        client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .sessionTimeoutMs(40000)
                .retryPolicy(retryPolicy)
                .namespace(ROOT_PATH)
                .build();
        client.start();

        log.info("zookeeper 连接成功");

    }



    @Override
    public void register(String serviceName, InetSocketAddress address) {
        try{
            //将一个service的名字创建为永久的地址,如果一个服务下限了,只删除地址不删除服务名
            if(client.checkExists().forPath("/"+serviceName)==null){
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/"+serviceName);
            }
            String path = "/" + serviceName + "/"  + getServiceAddress(address);
            //创建一个临时的节点,如果服务下线了,这个节点就会被删除
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);

        } catch (Exception e) {
            log.debug("此服务已存在");
        }
    }

    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        try {
            List<String> children = client.getChildren().forPath("/" + serviceName);
            String address = children.get(0);//暂时只用第一个
            return parseServiceName(address);
        } catch (Exception e) {
            log.error("未发现服务");
            throw new RuntimeException(e);
        }
    }

    String getServiceAddress(InetSocketAddress address) {
        return address.getHostString() + ":" + address.getPort();
    }
    InetSocketAddress parseServiceName(String serviceName) {
        String[] split = serviceName.split(":");
        return new InetSocketAddress(split[0], Integer.parseInt(split[1]));
    }

}
