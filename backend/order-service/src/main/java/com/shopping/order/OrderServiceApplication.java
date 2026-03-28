package com.shopping.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import io.seata.spring.boot.autoconfigure.SeataAutoConfiguration;

@SpringBootApplication(exclude = {SeataAutoConfiguration.class})
@EnableFeignClients
@MapperScan("com.shopping.order.mapper")
public class OrderServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
