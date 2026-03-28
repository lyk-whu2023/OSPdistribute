package com.shopping.order.config;

import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoDataSourceProxy
public class SeataConfig {
}
