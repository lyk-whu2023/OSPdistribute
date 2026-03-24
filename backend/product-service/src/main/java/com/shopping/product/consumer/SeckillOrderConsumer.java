package com.shopping.product.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.product.entity.SeckillProduct;
import com.shopping.product.mapper.SeckillProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeckillOrderConsumer {

    private final SeckillProductMapper seckillProductMapper;
    // JSON 工具：解析 Kafka 消息
    private final ObjectMapper objectMapper;

    /**
     * 监听 Kafka 主题：seckill-order
     * 一旦有秒杀成功消息，自动触发消费
     *
     * @param record Kafka 消息封装对象（key + value + 主题 + 分区等）
     */
    @KafkaListener(topics = "seckill-order", groupId = "seckill-order-group")
    public void consumeSeckillOrder(ConsumerRecord<String, String> record) {
        try {
            // 1. 获取 Kafka 消息体（真正的业务数据）
            String message = record.value();
            log.info("接收到秒杀订单消息：{}", message);
            // 2. 把 JSON 字符串消息 解析为 Map 方便取值
            Map<String, Object> orderData = objectMapper.readValue(message, Map.class);
            Long seckillProductId = Long.valueOf(orderData.get("seckillProductId").toString());
            Long userId = Long.valueOf(orderData.get("userId").toString());
            
            SeckillProduct seckillProduct = seckillProductMapper.selectById(seckillProductId);
            if (seckillProduct != null) {
                seckillProduct.setSold(seckillProduct.getSold() + 1);
                seckillProductMapper.updateById(seckillProduct);
                log.info("秒杀订单处理成功，seckillProductId: {}, userId: {}", seckillProductId, userId);
            } else {
                log.error("秒杀商品不存在，seckillProductId: {}", seckillProductId);
            }
        } catch (Exception e) {
            log.error("处理秒杀订单失败，message: {}, error: {}", record.value(), e.getMessage(), e);
        }
    }
}
