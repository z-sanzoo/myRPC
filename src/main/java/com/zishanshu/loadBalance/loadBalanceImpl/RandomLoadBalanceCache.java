package com.zishanshu.loadBalance.loadBalanceImpl;

import com.zishanshu.loadBalance.LoadBalanceCache;
import com.zishanshu.register.ServiceRegister;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@NoArgsConstructor
public class RandomLoadBalanceCache implements LoadBalanceCache {
    private static Random random = new Random();
    private final Map<String, Set<String>> addressSetMap = new HashMap<>();
    private final Map<String, List<String>> addressMap = new HashMap<>();

    @Override
    public synchronized Boolean contain(String serviceName) {
        return addressMap.containsKey(serviceName);
    }

    @Override
    public synchronized void initNode(String serviceName, List<String> addressList) {
        if(addressMap.containsKey(serviceName)){
            Set<String> oldSet = addressSetMap.get(serviceName);
            oldSet.addAll(addressList);
            addressMap.put(serviceName, new ArrayList<>(oldSet));
        }else {
            Set<String> set = new HashSet<>(addressList);
            addressSetMap.put(serviceName, set);
            addressMap.put(serviceName, new ArrayList<>(set));
        }
    }

    @Override
    public synchronized void addNode(String serviceName, String address) {
        if(!addressSetMap.get(serviceName).contains(address)){
            addressSetMap.get(serviceName).add(address);
            addressMap.get(serviceName).add(address);
        }
    }

    @Override
    public synchronized void removeNode(String serviceName, String address) {
        addressSetMap.get(serviceName).remove(address);
        addressMap.get(serviceName).remove(address);
        if(addressSetMap.get(serviceName).isEmpty()){
            addressSetMap.remove(serviceName);
            addressMap.remove(serviceName);
        }
    }

    @Override
    public synchronized String getNodeWithLoadBalance(String serviceName) {
        int choose = random.nextInt(addressSetMap.get(serviceName).size());
        return addressMap.get(serviceName).get(choose);
    }
}
