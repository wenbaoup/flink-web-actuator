package com.yijiupi.flink.actuator.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TableInfo {
    private String tableName;
    private String tableAlias;
    private List<ColumnInfo> columnInfo = new ArrayList<>();


}
