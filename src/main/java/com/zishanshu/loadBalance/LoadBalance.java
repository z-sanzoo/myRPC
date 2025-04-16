package com.zishanshu.loadBalance;

import java.util.List;
import java.util.Set;

public interface LoadBalance {
    String balance(Set<String> addressList);
}
