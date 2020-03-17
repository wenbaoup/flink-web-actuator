package com.yijiupi.flink.actuator.entity;

import lombok.Data;

import java.util.Objects;

@Data
public class ColumnInfo {
    private String sourceColumnName;
    private String sinkColumnName;
    private String sourceColumnType;
    private String sinkColumnType;
    private String tableAlias;
    private String tableName;
    private AggFunType aggFunType;
    private String udfName;
    private Boolean isSidePrimary;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ColumnInfo that = (ColumnInfo) o;
        return Objects.equals(sourceColumnName, that.sourceColumnName) &&
                Objects.equals(tableName, that.tableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceColumnName, tableName);
    }
}
