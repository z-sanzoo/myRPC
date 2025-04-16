package com.zishanshu.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class Decoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
        MessageType messageType = MessageType.getMessageTypeByCode(in.readShort())  ;
        if(messageType != MessageType.RPC_REQUEST &&
            messageType != MessageType.RPC_RESPONSE){
            log.error("非法消息类型");
            return;
        }
        short serializerType = in.readShort();
        Serializer serializer = Serializer.getSerializerByType(serializerType);
        if(serializer == null){
            log.error("不支持的序列化方式");
            return;
        }
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
//        log.debug(messageType.name());
        Object deserialize = serializer.deserialize(bytes,messageType);
        list.add(deserialize);
    }
}
