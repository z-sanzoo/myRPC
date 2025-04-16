package com.zishanshu.loadBalance.loadBalanceImpl;

import com.zishanshu.loadBalance.LoadBalanceCache;
import com.google.common.hash.Hashing;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.List;
import java.util.TreeMap;
import com.google.common.hash.Hashing;
import java.util.*;

class ConsistentHash<T> {
    private final TreeMap<Long, T> hashRing = new TreeMap<>(); // 哈希环
    private final int virtualNodeCount; // 每个物理节点的虚拟节点数

    public ConsistentHash(int virtualNodeCount) {
        this.virtualNodeCount = virtualNodeCount;
    }

    public void addNode(T node) {
        for (int i = 0; i < virtualNodeCount; i++) {
            String virtualNode = node.toString() + "#VN" + i;
            long hash = hash(virtualNode);
            hashRing.put(hash, node);
        }
    }

    public void removeNode(T node) {
        for (int i = 0; i < virtualNodeCount; i++) {
            String virtualNode = node.toString() + "#VN" + i;
            long hash = hash(virtualNode);
            hashRing.remove(hash);
        }
    }


    public T getNode(String key) {

        long hash = hash(key);
        // 找到第一个 >= hash 的节点
        SortedMap<Long, T> tailMap = hashRing.tailMap(hash);
        if (tailMap.isEmpty()) {
            // 如果没找到，返回环的第一个节点（环形结构）
            return hashRing.firstEntry().getValue();
        }
        return tailMap.get(tailMap.firstKey());
    }

    private long hash(String key) {
        return Hashing.murmur3_32().hashUnencodedChars(key).asInt() & 0xFFFFFFFFL;
    }
}


public class ConsistentHashLoadBalanceCache implements LoadBalanceCache {
    private static final int VIRTUAL_NODE_COUNT = 10; // 每个物理节点的虚拟节点数
    Map<String,ConsistentHash<String>> addressMap = new HashMap<>();

    @Override
    public synchronized Boolean contain(String serviceName) {
        return addressMap.containsKey(serviceName);
    }

    @Override
    public synchronized void initNode(String serviceName, List<String> addressList) {
        ConsistentHash<String> consistentHash = null;
        if (!addressMap.containsKey(serviceName)) {
            consistentHash = new ConsistentHash<>(VIRTUAL_NODE_COUNT);
            addressMap.put(serviceName, consistentHash);
        }
        for (String address : new HashSet<>(addressList)) {
            consistentHash.addNode(address);
        }
    }

    @Override
    public synchronized void addNode(String serviceName, String address) {
        ConsistentHash<String> consistentHash = addressMap.get(serviceName);
        if (consistentHash != null) {
            consistentHash.addNode(address);
        }
    }

    @Override
    public synchronized void removeNode(String serviceName, String address) {
        ConsistentHash<String> consistentHash = addressMap.get(serviceName);
        if (consistentHash != null) {
            consistentHash.removeNode(address);
        }
    }

    @Override
    public synchronized String getNodeWithLoadBalance(String serviceName)  {
        ConsistentHash<String> consistentHash = addressMap.get(serviceName);
        if (consistentHash == null) {
            return null; // 或者抛出异常
        }
        String MacAddress = getMacAddress();
        return consistentHash.getNode(MacAddress);

    }



    private String getMacAddress(){
        try {
            InetAddress ip = InetAddress.getLocalHost();
            byte[] mac = NetworkInterface.getByInetAddress(ip).getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (byte b : mac) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
