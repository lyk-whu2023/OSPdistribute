package com.shopping.product.config;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * 测试环境 Redis 和 Kafka 配置
 */
@Configuration
public class TestRedisConfig {

    @Bean
    @Primary
    @SuppressWarnings("unchecked")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        // 设置 key 的序列化器
        template.setKeySerializer(new StringRedisSerializer());
        // 设置 value 的序列化器
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    @Primary
    @SuppressWarnings("unchecked")
    public KafkaTemplate<String, String> kafkaTemplate() {
        // 使用 Mockito 创建 Mock，避免 Kafka 连接问题
        KafkaTemplate<String, String> mockKafka = Mockito.mock(KafkaTemplate.class, "kafkaTemplate");
        // 配置 send 方法的行为，返回成功的 CompletableFuture
        Mockito.lenient().when(mockKafka.send(Mockito.anyString(), Mockito.anyString()))
            .thenReturn(java.util.concurrent.CompletableFuture.completedFuture(null));
        return mockKafka;
    }
}
