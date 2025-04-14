package com.zishanshu.domain;

import lombok.*;

import java.io.Serializable;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Blog implements Serializable {
    private Integer id;
    private String title;
    private Integer userId;
}
