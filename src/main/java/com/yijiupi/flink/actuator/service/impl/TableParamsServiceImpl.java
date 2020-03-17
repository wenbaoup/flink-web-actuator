package com.yijiupi.flink.actuator.service.impl;

import com.yijiupi.flink.actuator.entity.TableParams;
import com.yijiupi.flink.actuator.mapper.TableParamsMapper;
import com.yijiupi.flink.actuator.service.ITableParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TableParamsServiceImpl implements ITableParamsService {

    @Autowired
    private TableParamsMapper tableParamsMapper;

    @Override
    public TableParams selectTableParams(Long id) {
        return tableParamsMapper.selectById(id);
    }
}
