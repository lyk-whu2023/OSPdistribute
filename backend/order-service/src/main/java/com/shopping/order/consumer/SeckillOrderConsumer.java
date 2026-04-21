package com.shopping.order.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.order.service.SeckillOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 秒杀订单消费者
 * 
 * 功能说明：
 * 1. 监听 Kafka 中的秒杀订单消息（来自 Product Service）
 * 2. 解析消息体，提取商品 ID、用户 ID 等信息
 * 3. 创建秒杀订单并更新商品销量
 * 
 * 消息格式：
 * {
 *   "seckillProductId": 9,
 *   "userId": 501,
 *   "timestamp": "2026-03-31T20:00:00"
 * }
 * 
 * @author system
 * @since 2026-03-31
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SeckillOrderConsumer {

    private final SeckillOrderService seckillOrderService;
    private final ObjectMapper objectMapper;

    /**
     * 监听秒杀订单 Topic
     * 
     * 处理流程：
     * 1. 接收 Kafka 消息（Product Service 发送）
     * 2. 解析 JSON 数据
     * 3. 验证数据完整性
     * 4. 创建秒杀订单
     * 5. 更新商品销量
     * 6. 记录处理结果
     * 
     * @param record Kafka 消息记录
     */
    @KafkaListener(
        topics = "seckill-order",
        groupId = "order-service",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeSeckillOrder(ConsumerRecord<String, String> record) {
        log.info("收到秒杀订单消息，topic: {}, partition: {}, offset: {}", 
            record.topic(), record.partition(), record.offset());
        
        try {
            // 1. 获取消息体
            String message = record.value();
            log.info("消息内容：{}", message);
            
            if (message == null || message.trim().isEmpty()) {
                log.warn("消息内容为空");
                return;
            }
            
            // 2. 解析 JSON 数据
            JsonNode jsonNode = objectMapper.readTree(message);
            
            Long seckillProductId = jsonNode.has("seckillProductId") 
                ? jsonNode.get("seckillProductId").asLong() 
                : null;
            
            Long userId = jsonNode.has("userId") 
                ? jsonNode.get("userId").asLong() 
                : null;
            
            String timestamp = jsonNode.has("timestamp") 
                ? jsonNode.get("timestamp").asText() 
                : null;
            
            log.info("解析消息：seckillProductId={}, userId={}, timestamp={}", 
                seckillProductId, userId, timestamp);
            
            // 3. 验证必要参数
            if (seckillProductId == null || userId == null) {
                log.error("消息缺少必要参数，seckillProductId: {}, userId: {}", 
                    seckillProductId, userId);
                return;
            }
            
            // 4. 创建秒杀订单（默认地址 ID 为 1，实际业务中需要从用户信息中获取）
            Long defaultAddressId = 1L;
            seckillOrderService.createSeckillOrder(
                seckillProductId, 
                userId, 
                defaultAddressId
            );
            
            log.info("秒杀订单创建成功，seckillProductId: {}, userId: {}", 
                seckillProductId, userId);
            
        } catch (Exception e) {
            log.error("处理秒杀订单消息失败，message: {}, error: {}", 
                record.value(), e.getMessage(), e);
            // 这里可以根据业务需求决定是否重试
            // 可以考虑将失败的消息发送到死信队列
        }
    }

    /**
     * 监听批量秒杀订单（可选功能）
     * 
     * 如果需要批量处理，可以使用 @KafkaListener 的 batch 模式
     * 
     * @param records 批量消息记录
     */
    // @KafkaListener(
    //     topics = "seckill-order-batch",
    //     groupId = "order-service-batch",
    //     containerFactory = "batchKafkaListenerContainerFactory"
    // )
    public void consumeBatchSeckillOrders(java.util.List<ConsumerRecord<String, String>> records) {
        log.info("收到批量秒杀订单消息，数量：{}", records.size());
        
        for (ConsumerRecord<String, String> record : records) {
            try {
                consumeSeckillOrder(record);
            } catch (Exception e) {
                log.error("处理单条批量消息失败，offset: {}, error: {}", 
                    record.offset(), e.getMessage(), e);
            }
        }
    }
}
