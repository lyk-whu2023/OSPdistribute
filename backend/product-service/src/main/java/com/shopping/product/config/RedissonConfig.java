package com.shopping.product.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RedissonConfig {
    
    @Value("${spring.data.redis.host}")
    private String redisHost;
    
    @Value("${spring.data.redis.port}")
    private int redisPort;
    
    @Value("${spring.data.redis.password:}")
    private String redisPassword;
    
    @Value("${spring.data.redis.database:0}")
    private int redisDatabase;
    
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        String address = "redis://" + redisHost + ":" + redisPort;
        
        config.useSingleServer()
            .setAddress(address)
            .setDatabase(redisDatabase)
            .setConnectionPoolSize(64)
            .setConnectionMinimumIdleSize(10)
            .setIdleConnectionTimeout(10000)
            .setConnectTimeout(3000)
            .setTimeout(3000);
        
        if (redisPassword != null && !redisPassword.isEmpty()) {
            config.useSingleServer().setPassword(redisPassword);
        }
        
        log.info("Redisson 客户端初始化完成，Redis 地址: {}", address);
        return Redisson.create(config);
    }
    
    @Bean
    public RBloomFilter<Long> seckillProductBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter("seckill:product:bloom");
        
        bloomFilter.tryInit(100000L, 0.01);
        
        log.info("秒杀商品布隆过滤器初始化完成，预期容量: 100000, 误判率: 1%");
        return bloomFilter;
    }
}
