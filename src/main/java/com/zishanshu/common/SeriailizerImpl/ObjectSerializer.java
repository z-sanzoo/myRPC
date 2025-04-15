package com.zishanshu.common.SeriailizerImpl;

import com.zishanshu.common.MessageType;
import com.zishanshu.common.Serializer;

import java.io.*;

public class ObjectSerializer implements Serializer {


    @Override
    public byte[] serialize(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try{
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }

    @Override
    public Object deserialize(byte[] data, MessageType messageType) {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        try{
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object obj = ois.readObject();
            ois.close();
            bis.close();
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public int getType() {
        return 0;
    }
    //表示原生的序列化器
}
