package com.example.backend.service;

import com.example.backend.dto.request.CategoryRequest;
import com.example.backend.dto.response.CategoryResponse;
import com.example.backend.model.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    List<CategoryResponse> findAll();

    CategoryResponse findById(UUID id);

    Category addCategory(CategoryRequest request);

    void deleteCategory(UUID id);

    CategoryResponse addSubcategory(CategoryRequest subcategoryRequest);

    List<UUID> findAllChildCategoryIds(UUID parentId);
}