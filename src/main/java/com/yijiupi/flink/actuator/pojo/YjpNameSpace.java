package com.yijiupi.flink.actuator.pojo;

import lombok.Data;

import java.sql.Date;

@Data
public class YjpNameSpace {
    private Long id;
    private String sys;
    private String database;
    private String version;
    private Long databaseId;
    private Integer active;
    private String desc;
    private Date createTime;
    private Long createBy;
    private Long updateTime;
    private Long updateBy;


}
