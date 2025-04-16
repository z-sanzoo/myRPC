package com.zishanshu.loadBalance.loadBalanceImpl;

import com.zishanshu.loadBalance.LoadBalanceCache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LRULoadBalanceCache implements LoadBalanceCache {
    class Node {
        String address;
        Node pre;
        Node next;

        public Node(String address) {
            this.address = address;
        }
    }

    private final Node head = new Node(null);
    private final Node tail = new Node(null);
    private final Map<String, Integer> addressServiceCnt = new HashMap<>();
    private final Map<String, Set<String>> addressMap = new HashMap<>();

    public  LRULoadBalanceCache(){
        head.next = tail;
        tail.pre = head;
    }

    @Override
    public synchronized  Boolean contain(String serviceName) {
        return addressMap.containsKey(serviceName);
    }

    @Override
    public synchronized void initNode(String serviceName, List<String> addressList) {
        Set<String> set = new HashSet<>(addressList);
        if (!addressMap.containsKey(serviceName)) {
            addressMap.put(serviceName,set);
        }
        for(String address : set) {
            if (!addressServiceCnt.containsKey(address)) {
                addressServiceCnt.put(address, 1);
                Node LastNode = tail.pre;
                Node newNode = new Node(address);
                LastNode.next = newNode;
                newNode.pre = LastNode;
                newNode.next = tail;
                tail.pre = newNode;
            } else {
                addressServiceCnt.put(address, addressServiceCnt.get(address) + 1);
            }
        }
    }

    @Override
    public synchronized void addNode(String serviceName, String address) {
        if(!addressMap.get(serviceName).contains(address)) {
            addressMap.get(serviceName).add(address);
            if (!addressServiceCnt.containsKey(address)) {
                addressServiceCnt.put(address, 1);
                Node LastNode = tail.pre;
                Node newNode = new Node(address);
                LastNode.next = newNode;
                newNode.pre = LastNode;
                newNode.next = tail;
                tail.pre = newNode;
            } else {
                addressServiceCnt.put(address, addressServiceCnt.get(address) + 1);
            }
        }
    }

    @Override
    public synchronized void removeNode(String serviceName, String address) {
        if (addressMap.get(serviceName).contains(address)) {
            addressMap.get(serviceName).remove(address);
            if(addressMap.get(serviceName).isEmpty()) {
                addressMap.remove(serviceName);
            }
            addressServiceCnt.put(address, addressServiceCnt.get(address) - 1);
            if (addressServiceCnt.get(address) == 0) {
                addressServiceCnt.remove(address);
                for(Node node = head; node != null; node = node.next) {
                    if (node.address.equals(address)) {
                        node.pre.next = node.next;
                        node.next.pre = node.pre;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public synchronized String getNodeWithLoadBalance(String serviceName) {
        for(Node node=tail; node != head; node = node.pre) {
            if (addressMap.get(serviceName).contains(node.address)) {
                node.pre.next = node.next;
                node.next.pre = node.pre;
                Node firstNode = head.next;
                head.next = node;
                node.pre = head;
                node.next = firstNode;
                firstNode.pre = node;
                return node.address;
            }
        }
        return null;
    }
}
