package com.yijiupi.flink.actuator.pojo;

import lombok.Data;

import java.sql.Date;

@Data
public class YjpColumn {
    private Long id;
    private Long tableId;
    private String column;
    private String type;
    private String version;
    private Long columnId;
    private Integer active;
    private String desc;
    private Date createTime;
    private Long createBy;
    private Long updateTime;
    private Long updateBy;


}