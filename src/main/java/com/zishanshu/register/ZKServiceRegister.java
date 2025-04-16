package com.zishanshu.register;

import com.zishanshu.loadBalance.LoadBalance;
import com.zishanshu.loadBalance.loadBalanceImpl.RandomLoadBalance;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class ZKServiceRegister implements ServiceRegister {
    private static final String ROOT_PATH = "myRPC";
    private final CuratorFramework client;
    private final LoadBalance loadBalance;
    private final HashMap<String, Set<String>> AddressCache = new HashMap<>();


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

        loadBalance = new RandomLoadBalance();
//        log.info("zookeeper 连接成功");

    }



    @Override
    public void register(String serviceName, InetSocketAddress address) {
        try{
            //将一个service的名字创建为永久的地址,如果一个服务下限了,只删除地址不删除服务名
            if(client.checkExists().forPath("/"+serviceName)==null){
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/"+serviceName);
                log.info("创建服务节点成功");
            }
        }catch (Exception e){
            log.debug("创建服务节点失败");
        }
        try{
            String path = "/" + serviceName + "/"  + getServiceAddress(address);
            //创建一个临时的节点,如果服务下线了,这个节点就会被删除
            client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
        } catch (Exception e) {
            log.debug("服务节点已存在");
        }
    }

    @Override
    public void deregister() {
        client.close();
    }

    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {
        Set<String> children = null;

        // 先从缓存中获取
        if (AddressCache.containsKey(serviceName)) {
            // 缓存中已经存在, watch机制已经确保它是最新的
            children = AddressCache.get(serviceName);
            log.debug("服务已缓存");
        } else {
            // 如果不存在说明以前没有注册过这个服务, 需要去zookeeper中获取
            // 同时也没有watch机制, 需要手动添加
            try {
                children = new HashSet<>(client.getChildren().forPath("/" + serviceName)) ;
                AddressCache.put(serviceName, children);
            } catch (Exception e) {
                log.error("未发现服务");
            }
            // 注册一个watcher异步更新AddressCache
            registerWatcher(serviceName);
        }

        String address = loadBalance.balance(children);
        return parseServiceAddress(address);
    }

    String getServiceAddress(InetSocketAddress address) {
        return address.getHostString() + ":" + address.getPort();
    }

    InetSocketAddress parseServiceAddress(String serviceName) {
        String[] split = serviceName.split(":");
        return new InetSocketAddress(split[0], Integer.parseInt(split[1]));
    }

    public void registerWatcher(String serviceName) {
        CuratorCache curatorCache = CuratorCache.build(client, "/"+serviceName);
        CuratorCacheListener listener = CuratorCacheListener.builder()
                .forPathChildrenCache("/" + serviceName, client, new PathChildrenCacheListener() {
                    @Override
                    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) {
                        // 获取事件类型及子节点路径
                        String fullPath = event.getData().getPath(); // 完整路径（如 "/serviceName/child1"）
                        String eventAddress = fullPath.substring(fullPath.lastIndexOf('/') + 1);

                        switch (event.getType()) {
                            case CHILD_ADDED:
                                log.info("{}的子节点添加: {}", serviceName, event.getData().getPath());
                                // 把新的子节点添加到缓存中,把Path最后一个/之后的字串作为地址
                                AddressCache.get(serviceName).add(eventAddress);
                                break;
                            case CHILD_REMOVED:
                                log.info("{}的子节点删除: {}", serviceName, event.getData().getPath());
                                AddressCache.get(serviceName).remove(eventAddress);
                                break;
                            default:
                                break;
                        }
                    }
                })
                .build();
        curatorCache.listenable().addListener(listener);
        curatorCache.start();
    }

}
