package com.zishanshu.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Encoder extends MessageToByteEncoder {
    private Serializer serializer;
    // 消息的格式: 先16位的消息类型(RPC_RESPONSE/RPC_REQUEST), 16位的序列化方式(普通序列化/json), 4字节的消息长度, 消息体
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if(msg instanceof RPCRequest){
            out.writeShort(MessageType.RPC_REQUEST.getCode());
        }else if(msg instanceof RPCResponse){
            out.writeShort(MessageType.RPC_RESPONSE.getCode());
        }
        out.writeShort(serializer.getType());
        byte[] serialize = serializer.serialize(msg);
        out.writeInt(serialize.length);
        out.writeBytes(serialize);
    }
}
