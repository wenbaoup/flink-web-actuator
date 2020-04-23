package com.yijiupi.flink.actuator.pojo;

import lombok.Data;

import java.sql.Date;

@Data
public class YjpTable {
    private Long id;
    private Long databaseId;
    private String table;
    private Integer isSide;
    private String type;
    private String version;
    private String primaryKey;
    private Long tableId;
    private String address;
    private Integer active;
    private String desc;
    private Date createTime;
    private Long createBy;
    private Long updateTime;
    private Long updateBy;


}
