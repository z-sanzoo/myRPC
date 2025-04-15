package com.zishanshu.common;

public enum MessageType {
    RPC_REQUEST(0),
    RPC_RESPONSE(1);


    private int code;

    MessageType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static MessageType getMessageTypeByCode(int code) {
        for (MessageType messageType : MessageType.values()) {
            if (messageType.getCode() == code) {
                return messageType;
            }
        }
        return null;
    }
}
