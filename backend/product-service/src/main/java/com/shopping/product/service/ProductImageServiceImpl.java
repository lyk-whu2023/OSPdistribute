package com.shopping.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shopping.product.dto.request.ProductImageRequest;
import com.shopping.product.dto.response.ProductImageResponse;
import com.shopping.product.entity.ProductImage;
import com.shopping.product.mapper.ProductImageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageMapper productImageMapper;

    @Override
    public List<ProductImageResponse> getProductImages(Long productId) {
        log.info("查询商品图片，productId: {}", productId);
        List<ProductImage> images = productImageMapper.selectList(
            new LambdaQueryWrapper<ProductImage>()
                .eq(ProductImage::getProductId, productId)
                .orderByAsc(ProductImage::getSort)
        );
        log.info("查询到{}条商品图片记录，productId: {}", images.size(), productId);
        return images.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    @Override
    public ProductImageResponse getProductImage(Long id) {
        log.info("查询商品图片详情，id: {}", id);
        ProductImage image = productImageMapper.selectById(id);
        if (image == null) {
            log.warn("商品图片不存在，id: {}", id);
            throw new RuntimeException("商品图片不存在");
        }
        return convertToResponse(image);
    }

    @Override
    @Transactional
    public ProductImageResponse addProductImage(ProductImageRequest request) {
        log.info("添加商品图片，productId: {}, imageUrl: {}", request.getProductId(), request.getImageUrl());
        
        if (request.getIsMain() != null && request.getIsMain() == 1) {
            cancelMainImage(request.getProductId());
        }
        
        ProductImage image = new ProductImage();
        BeanUtils.copyProperties(request, image);
        image.setCreateTime(LocalDateTime.now());
        productImageMapper.insert(image);
        
        log.info("商品图片添加成功，id: {}", image.getId());
        return convertToResponse(image);
    }

    @Override
    @Transactional
    public void updateProductImage(Long id, ProductImageRequest request) {
        log.info("更新商品图片，id: {}, imageUrl: {}", id, request.getImageUrl());
        
        ProductImage image = productImageMapper.selectById(id);
        if (image == null) {
            log.warn("商品图片不存在，id: {}", id);
            throw new RuntimeException("商品图片不存在");
        }
        
        if (request.getIsMain() != null && request.getIsMain() == 1 && !image.getIsMain().equals(1)) {
            cancelMainImage(image.getProductId());
        }
        
        BeanUtils.copyProperties(request, image);
        productImageMapper.updateById(image);
        
        log.info("商品图片更新成功，id: {}", id);
    }

    @Override
    @Transactional
    public void deleteProductImage(Long id) {
        log.info("删除商品图片，id: {}", id);
        ProductImage image = productImageMapper.selectById(id);
        if (image != null) {
            productImageMapper.deleteById(id);
            log.info("商品图片删除成功，id: {}", id);
        } else {
            log.warn("商品图片不存在，id: {}", id);
        }
    }

    @Override
    @Transactional
    public void setMainImage(Long productId, Long imageId) {
        log.info("设置商品主图，productId: {}, imageId: {}", productId, imageId);
        
        cancelMainImage(productId);
        
        ProductImage image = productImageMapper.selectById(imageId);
        if (image != null && image.getProductId().equals(productId)) {
            image.setIsMain(1);
            productImageMapper.updateById(image);
            log.info("商品主图设置成功，imageId: {}", imageId);
        } else {
            log.error("设置主图失败，图片不存在或不属于该商品，imageId: {}", imageId);
            throw new RuntimeException("图片不存在或不属于该商品");
        }
    }

    private void cancelMainImage(Long productId) {
        List<ProductImage> mainImages = productImageMapper.selectList(
            new LambdaQueryWrapper<ProductImage>()
                .eq(ProductImage::getProductId, productId)
                .eq(ProductImage::getIsMain, 1)
        );
        
        for (ProductImage image : mainImages) {
            image.setIsMain(0);
            productImageMapper.updateById(image);
        }
        
        if (!mainImages.isEmpty()) {
            log.info("已取消{}张主图标记，productId: {}", mainImages.size(), productId);
        }
    }

    private ProductImageResponse convertToResponse(ProductImage image) {
        if (image == null) {
            return null;
        }
        ProductImageResponse response = new ProductImageResponse();
        BeanUtils.copyProperties(image, response);
        return response;
    }
}
