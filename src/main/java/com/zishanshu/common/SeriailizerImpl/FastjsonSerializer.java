package com.zishanshu.common.SeriailizerImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.zishanshu.common.MessageType;
import com.zishanshu.common.RPCRequest;
import com.zishanshu.common.RPCResponse;
import com.zishanshu.common.Serializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FastjsonSerializer implements Serializer {
    static {
        ParserConfig.getGlobalInstance().addAccept("com.zishanshu.domain.");
    }

    @Override
    public byte[] serialize(Object obj) {
        return JSONObject.toJSONBytes(obj);
    }

    @Override
    public Object deserialize(byte[] bytes, MessageType messageType) {
        Object obj = null;
        switch (messageType){
            case RPC_REQUEST:
//                for(byte b : bytes){
//                    System.out.print((char)b);
//                }
                RPCRequest request = JSON.parseObject(bytes, RPCRequest.class);
                Object[] objects = new Object[request.getParams().length];
                for(int i=0;i<objects.length;i++){
                    Class<?> paramsType = request.getParamTypes()[i];
                    if (!paramsType.isAssignableFrom(request.getParams()[i].getClass())){
                        objects[i] = JSONObject.toJavaObject((JSONObject) request.getParams()[i],request.getParamTypes()[i]);
                    }else{
                        objects[i] = request.getParams()[i];
                    }
                }
                request.setParams(objects);
                obj = request;
                break;

            case RPC_RESPONSE:
//                for(byte b : bytes){
//                    System.out.print((char)b);
//                }
                RPCResponse response = JSON.parseObject(bytes, RPCResponse.class);
//                log.debug(response.toString());

                Class<?> dataType = response.getDataType();
                if(! dataType.isAssignableFrom(response.getData().getClass())){
                    response.setData(JSONObject.toJavaObject((JSONObject) response.getData(),dataType));
                }
                obj = response;
                break;
            default:
                log.error("暂时不支持此种消息");
                throw new RuntimeException();

        }
        return obj;
    }

    @Override
    public int getType() {
        return 1;
    }
}
