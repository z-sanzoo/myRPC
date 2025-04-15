package com.zishanshu.common;


import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class RPCRequest implements Serializable {
    private String interfaceName;
    private String methodName;
    private Class<?>[] paramTypes;
    private Object[] params;
}
