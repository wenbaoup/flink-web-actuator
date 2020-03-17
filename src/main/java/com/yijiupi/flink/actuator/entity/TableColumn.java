package com.yijiupi.flink.actuator.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;

@Data
public class TableColumn {
    private Long id;

    @TableField("table_id")
    private Long tableId;
    private String name;
    private Integer type;
    private String desc;
}
