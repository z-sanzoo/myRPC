package com.zishanshu.common;

import com.zishanshu.common.SeriailizerImpl.FastjsonSerializer;
import com.zishanshu.common.SeriailizerImpl.ObjectSerializer;
import com.zishanshu.common.SeriailizerImpl.ProtobufSerializer;

public interface Serializer {

    // 把对象序列化成字节数组
    byte[] serialize(Object obj);

    //从字节数组反序化成消息,
    Object deserialize(byte[] data, MessageType messageType);

    int getType();

    static Serializer getSerializerByType(int code){
        switch(code){
            case 0:
                return new ObjectSerializer();
            case 1:
                return new FastjsonSerializer();
            case 2:
                return new ProtobufSerializer();
            default:
                return null;
        }

    }
}
