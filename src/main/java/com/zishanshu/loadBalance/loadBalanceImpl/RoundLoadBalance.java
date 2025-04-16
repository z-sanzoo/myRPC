package com.zishanshu.loadBalance.loadBalanceImpl;

import com.zishanshu.loadBalance.LoadBalance;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RoundLoadBalance implements LoadBalance {
    private int choose = -1;

    @Override
    public String balance(Set<String> addressList) {
        choose++;
        choose = choose%addressList.size();
        String ithElement = null;
        Iterator<String> iterator = addressList.iterator();
        for (int j = 0; j <= choose && iterator.hasNext(); j++) {
            ithElement = iterator.next();
        }
        return ithElement;
    }
}