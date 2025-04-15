package com.zishanshu.common.SeriailizerImpl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zishanshu.common.MessageType;
import com.zishanshu.common.RPCRequest;
import com.zishanshu.common.RPCResponse;
import com.zishanshu.common.Serializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProtobufSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        Schema schema = RuntimeSchema.getSchema(obj.getClass());

        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        byte[] bytes;
        try {
            bytes =  ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }
        return bytes;
    }


    @Override
    public Object deserialize(byte[] data, MessageType messageType) {
        Object obj = null;
        Schema schema = null;
        try {
            switch (messageType){
                case RPC_REQUEST:
                    schema = RuntimeSchema.getSchema(RPCRequest.class);
                    obj = RPCRequest.class.getDeclaredConstructor().newInstance();
                    break;
                case RPC_RESPONSE:
                    schema = RuntimeSchema.getSchema(RPCResponse.class);
                    obj = RPCResponse.class.getDeclaredConstructor().newInstance();
                    break;
                default:
                    log.error("暂时不支持此种消息");
                    throw new RuntimeException();
            }
        } catch (Exception e) {
            log.error("反序列化失败", e);
            throw new RuntimeException();
        }
        ProtostuffIOUtil.mergeFrom(data, obj, schema);
        return obj;
    }

    @Override
    public int getType() {
        return 2;
    }
}
