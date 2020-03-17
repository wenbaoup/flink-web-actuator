package com.yijiupi.flink.actuator;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yijiupi.flink.actuator.mapper")
public class FlinkWebActuatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlinkWebActuatorApplication.class, args);
    }

}
