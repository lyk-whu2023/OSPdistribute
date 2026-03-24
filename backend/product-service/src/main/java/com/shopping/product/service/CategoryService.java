package com.shopping.product.service;

import com.shopping.product.dto.request.CategoryRequest;
import com.shopping.product.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {

    List<CategoryResponse> getAllCategories();

    List<CategoryResponse> getCategoriesByLevel(Integer level);

    List<CategoryResponse> getChildCategories(Long parentId);

    CategoryResponse getCategoryById(Long id);

    CategoryResponse createCategory(CategoryRequest request);

    CategoryResponse updateCategory(Long id, CategoryRequest request);

    void deleteCategory(Long id);
}