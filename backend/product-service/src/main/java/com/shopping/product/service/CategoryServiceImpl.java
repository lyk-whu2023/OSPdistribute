package com.shopping.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shopping.product.dto.request.CategoryRequest;
import com.shopping.product.dto.response.CategoryResponse;
import com.shopping.product.entity.Category;
import com.shopping.product.mapper.CategoryMapper;
import com.shopping.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryResponse> getAllCategories() {
        log.info("查询所有分类");
        List<CategoryResponse> categories = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                        .eq(Category::getStatus,1)
                        .orderByAsc(Category::getSort))
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        log.info("查询到{}个分类", categories.size());
        return categories;
    }

    @Override
    public List<CategoryResponse> getCategoriesByLevel(Integer level) {
        log.info("按级别查询分类，level: {}", level);
        List<CategoryResponse> categories = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                        .eq(Category::getLevel, level)
                        .eq(Category::getStatus, 1)
                        .orderByAsc(Category::getSort))
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        log.info("查询到{}个分类", categories.size());
        return categories;
    }

    @Override
    public List<CategoryResponse> getChildCategories(Long parentId) {
        log.info("查询子分类，parentId: {}", parentId);
        List<CategoryResponse> categories = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                        .eq(Category::getParentId, parentId)
                        .eq(Category::getStatus, 1)
                        .orderByAsc(Category::getSort))
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        log.info("查询到{}个子分类", categories.size());
        return categories;
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        log.info("查询分类详情，id: {}", id);
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            log.warn("分类不存在，id: {}", id);
            throw new RuntimeException("分类不存在");
        }
        return convertToResponse(category);
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        log.info("创建分类，name: {}, level: {}", request.getName(), request.getLevel());
        Category category = new Category();
        BeanUtils.copyProperties(request, category);
        category.setStatus(1);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.insert(category);
        log.info("分类创建成功，id: {}", category.getId());
        return convertToResponse(category);
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        log.info("更新分类，id: {}, name: {}", id, request.getName());
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            log.warn("分类不存在，id: {}", id);
            throw new RuntimeException("分类不存在");
        }

        BeanUtils.copyProperties(request, category);
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.updateById(category);
        log.info("分类更新成功，id: {}", id);
        return convertToResponse(category);
    }

    @Override
    public void deleteCategory(Long id) {
        log.info("删除分类，id: {}", id);
        categoryMapper.deleteById(id);
        log.info("分类删除成功，id: {}", id);
    }

    private CategoryResponse convertToResponse(Category category) {
        if (category == null) {
            return null;
        }
        CategoryResponse response = new CategoryResponse();
        BeanUtils.copyProperties(category, response);
        return response;
    }
}