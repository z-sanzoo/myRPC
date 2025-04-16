package com.zishanshu.loadBalance.loadBalanceImpl;


import com.zishanshu.loadBalance.LoadBalanceCache;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
@Slf4j
public class RoundLoadBalanceCache implements LoadBalanceCache{
    private final Map<String, Integer> addressChoose = new HashMap<>();
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
            addressChoose.put(serviceName, -1);
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
        int c = addressChoose.get(serviceName);
        int nextChoose = (c + 1) % addressSetMap.get(serviceName).size();
        log.debug(String.valueOf(nextChoose));
        addressChoose.put(serviceName,nextChoose);
        return addressMap.get(serviceName).get(nextChoose);
    }
}