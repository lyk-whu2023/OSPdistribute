package com.shopping.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shopping.product.dto.request.ProductRequest;
import com.shopping.product.dto.response.ProductResponse;
import com.shopping.product.entity.Product;
import com.shopping.product.mapper.ProductMapper;
import com.shopping.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public Page<ProductResponse> getProducts(Integer page, Integer size) {
        log.info("查询商品列表，page: {}, size: {}", page, size);
        Page<Product> productPage = productMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getStatus, 1)
                        .orderByDesc(Product::getSales));
        
        Page<ProductResponse> responsePage = new Page<>(page, size, productPage.getTotal());
        responsePage.setRecords(productPage.getRecords().stream()
                .map(this::convertToResponse)
                .toList());
        log.info("查询到{}条商品记录", responsePage.getTotal());
        return responsePage;
    }

    @Override
    public ProductResponse getProductById(Long id) {
        log.info("查询商品详情，id: {}", id);
        String cacheKey = "product:" + id;
        Product product = (Product) redisTemplate.opsForValue().get(cacheKey);

        if (product == null) {
            log.info("缓存未命中，从数据库查询，id: {}", id);
            product = productMapper.selectById(id);
            if (product != null) {
                redisTemplate.opsForValue().set(cacheKey, product, 1, TimeUnit.HOURS);
                log.info("商品详情已缓存，id: {}", id);
            }else {
                log.warn("商品不存在，id: {}", id);
                throw new RuntimeException("商品不存在");
            }
        } else {
            log.info("缓存命中，id: {}", id);
        }

        return convertToResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("创建商品，name: {}, price: {}", request.getName(), request.getPrice());
        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        product.setStatus(1);
        product.setSales(0);
        product.setCreateTime(LocalDateTime.now());
        product.setUpdateTime(LocalDateTime.now());
        productMapper.insert(product);
        log.info("商品创建成功，id: {}", product.getId());
        return convertToResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("更新商品，id: {}, name: {}", id, request.getName());
        Product product = productMapper.selectById(id);
        if (product == null) {
            log.warn("商品不存在，id: {}", id);
            throw new RuntimeException("商品不存在");
        }

        BeanUtils.copyProperties(request, product);
        product.setUpdateTime(LocalDateTime.now());
        productMapper.updateById(product);

        String cacheKey = "product:" + id;
        redisTemplate.delete(cacheKey);
        log.info("商品更新成功，id: {}, 缓存已清理", id);

        return convertToResponse(product);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        log.info("删除商品，id: {}", id);
        productMapper.deleteById(id);
        String cacheKey = "product:" + id;
        redisTemplate.delete(cacheKey);
        log.info("商品删除成功，id: {}, 缓存已清理", id);
    }

    @Override
    @Transactional
    public void updateStock(Long productId, Integer quantity) {
        log.info("更新商品库存，productId: {}, quantity: {}", productId, quantity);
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStock() < quantity) {
            log.warn("库存不足，productId: {}, stock: {}", productId, product != null ? product.getStock() : 0);
            throw new RuntimeException("库存不足");
        }

        product.setStock(product.getStock() - quantity);
        product.setSales(product.getSales() + quantity);
        productMapper.updateById(product);

        kafkaTemplate.send("stock-update", "product:" + productId + ":" + quantity);
        log.info("库存更新成功，productId: {}, 剩余库存：{}", productId, product.getStock());
    }

    private ProductResponse convertToResponse(Product product) {
        if (product == null) {
            return null;
        }
        ProductResponse response = new ProductResponse();
        BeanUtils.copyProperties(product, response);
        return response;
    }
}