package com.zishanshu.loadBalance.loadBalanceImpl;

import com.zishanshu.loadBalance.LoadBalance;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomLoadBalance implements LoadBalance {
    private static Random random = new Random();
    @Override
    public String balance(Set<String> addressList) {
        int choose = random.nextInt(addressList.size());
        String ithElement = null;
        Iterator<String> iterator = addressList.iterator();
        for (int j = 0; j <= choose && iterator.hasNext(); j++) {
            ithElement = iterator.next();
        }
        return ithElement;
    }
}
