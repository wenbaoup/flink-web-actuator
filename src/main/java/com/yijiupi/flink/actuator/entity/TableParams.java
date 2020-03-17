package com.yijiupi.flink.actuator.entity;

import lombok.Data;

@Data
public class TableParams {
    private Long id;
    private String name;
    private Integer type;
    private String  primary;
    private String desc;
}
