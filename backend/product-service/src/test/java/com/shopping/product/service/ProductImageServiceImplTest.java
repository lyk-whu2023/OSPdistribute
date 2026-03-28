package com.shopping.product.service;

import com.shopping.product.dto.request.ProductImageRequest;
import com.shopping.product.dto.response.ProductImageResponse;
import com.shopping.product.entity.ProductImage;
import com.shopping.product.mapper.ProductImageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductImageServiceImplTest {

    @Mock
    private ProductImageMapper productImageMapper;

    @InjectMocks
    private ProductImageServiceImpl productImageService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testGetProductImages() {
        Long productId = 1L;
        List<ProductImage> images = new ArrayList<>();
        
        ProductImage image1 = new ProductImage();
        image1.setId(1L);
        image1.setProductId(productId);
        image1.setImageUrl("http://example.com/image1.jpg");
        image1.setSort(1);
        image1.setIsMain(1);
        
        ProductImage image2 = new ProductImage();
        image2.setId(2L);
        image2.setProductId(productId);
        image2.setImageUrl("http://example.com/image2.jpg");
        image2.setSort(2);
        image2.setIsMain(0);
        
        images.add(image1);
        images.add(image2);

        when(productImageMapper.selectList(any())).thenReturn(images);

        List<ProductImageResponse> responses = productImageService.getProductImages(productId);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(productImageMapper).selectList(any());
    }

    @Test
    void testAddProductImage_NotMain() {
        ProductImageRequest request = new ProductImageRequest();
        request.setProductId(1L);
        request.setImageUrl("http://example.com/new.jpg");
        request.setSort(3);
        request.setIsMain(0);

        ProductImage savedImage = new ProductImage();
        savedImage.setId(3L);
        savedImage.setProductId(1L);
        savedImage.setImageUrl("http://example.com/new.jpg");
        savedImage.setSort(3);
        savedImage.setIsMain(0);

        when(productImageMapper.insert(any(ProductImage.class))).thenAnswer(invocation -> {
            ProductImage image = invocation.getArgument(0);
            image.setId(3L);
            image.setCreateTime(LocalDateTime.now());
            return 1;
        });

        ProductImageResponse response = productImageService.addProductImage(request);

        assertNotNull(response);
        assertEquals(3L, response.getId());
        verify(productImageMapper).insert(any(ProductImage.class));
    }

    @Test
    void testAddProductImage_AsMain() {
        ProductImageRequest request = new ProductImageRequest();
        request.setProductId(1L);
        request.setImageUrl("http://example.com/main.jpg");
        request.setSort(1);
        request.setIsMain(1);

        List<ProductImage> existingMainImages = new ArrayList<>();
        ProductImage existingMain = new ProductImage();
        existingMain.setId(1L);
        existingMain.setIsMain(1);
        existingMainImages.add(existingMain);

        when(productImageMapper.selectList(any())).thenReturn(existingMainImages);
        when(productImageMapper.updateById(any(ProductImage.class))).thenReturn(1);
        when(productImageMapper.insert(any(ProductImage.class))).thenAnswer(invocation -> {
            ProductImage image = invocation.getArgument(0);
            image.setId(3L);
            return 1;
        });

        ProductImageResponse response = productImageService.addProductImage(request);

        assertNotNull(response);
        assertEquals(1, response.getIsMain());
        verify(productImageMapper, times(1)).updateById(any(ProductImage.class));
        verify(productImageMapper).insert(any(ProductImage.class));
    }

    @Test
    void testSetMainImage() {
        Long productId = 1L;
        Long imageId = 2L;

        ProductImage image = new ProductImage();
        image.setId(imageId);
        image.setProductId(productId);
        image.setIsMain(0);

        when(productImageMapper.selectById(imageId)).thenReturn(image);
        when(productImageMapper.selectList(any())).thenReturn(new ArrayList<>());
        when(productImageMapper.updateById(any(ProductImage.class))).thenReturn(1);

        assertDoesNotThrow(() -> productImageService.setMainImage(productId, imageId));
        assertEquals(1, image.getIsMain());
        verify(productImageMapper).updateById(image);
    }

    @Test
    void testDeleteProductImage() {
        Long imageId = 1L;
        ProductImage image = new ProductImage();
        image.setId(imageId);
        
        when(productImageMapper.selectById(imageId)).thenReturn(image);
        when(productImageMapper.deleteById(imageId)).thenReturn(1);

        assertDoesNotThrow(() -> productImageService.deleteProductImage(imageId));
        verify(productImageMapper).selectById(imageId);
        verify(productImageMapper).deleteById(imageId);
    }

    @Test
    void testUpdateProductImage() {
        Long imageId = 1L;
        ProductImageRequest request = new ProductImageRequest();
        request.setImageUrl("http://example.com/updated.jpg");
        request.setSort(2);

        ProductImage existingImage = new ProductImage();
        existingImage.setId(imageId);
        existingImage.setProductId(1L);
        existingImage.setIsMain(0);

        when(productImageMapper.selectById(imageId)).thenReturn(existingImage);
        when(productImageMapper.updateById(any(ProductImage.class))).thenReturn(1);

        assertDoesNotThrow(() -> productImageService.updateProductImage(imageId, request));
        assertEquals("http://example.com/updated.jpg", existingImage.getImageUrl());
        verify(productImageMapper).updateById(existingImage);
    }
}
 