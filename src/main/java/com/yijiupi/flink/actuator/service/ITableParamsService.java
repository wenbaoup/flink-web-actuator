package com.yijiupi.flink.actuator.service;

import com.yijiupi.flink.actuator.entity.TableParams;


public interface ITableParamsService {

    TableParams selectTableParams(Long id);
}
