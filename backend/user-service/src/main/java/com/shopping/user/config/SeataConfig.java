package com.shopping.user.config;

import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoDataSourceProxy
public class SeataConfig {
}
