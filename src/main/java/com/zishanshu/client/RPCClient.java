package com.zishanshu.client;

import com.zishanshu.common.RPCRequest;
import com.zishanshu.common.RPCResponse;

public interface RPCClient {
    RPCResponse sendRequest(RPCRequest rpcRequest);
}
