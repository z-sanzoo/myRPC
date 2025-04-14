package com.zishanshu.domain;
import lombok.*;

import java.io.Serializable;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User implements Serializable {
    private Integer id;
    private String name;
    private Boolean sex;

}
