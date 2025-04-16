package com.zishanshu.loadBalance;

import java.util.List;
import java.util.Set;

public interface LoadBalanceCache {
    Boolean contain(String serviceName);
    void initNode(String serviceName, List<String> addressList);
    void addNode(String serviceName, String address);
    void removeNode(String serviceName, String address);
    String getNodeWithLoadBalance(String serviceName);
}
