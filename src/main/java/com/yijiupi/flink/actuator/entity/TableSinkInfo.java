package com.yijiupi.flink.actuator.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TableSinkInfo {
    private String tableName;
    private List<ColumnInfo> list = new ArrayList<>();
}
