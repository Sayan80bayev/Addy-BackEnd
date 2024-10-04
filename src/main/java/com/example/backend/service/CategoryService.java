package com.example.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.dto.request.CategoryRequest;
import com.example.backend.dto.response.CategoryResponse;
import com.example.backend.mapper.CategoryMapper;
import com.example.backend.model.Category;
import com.example.backend.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository repository;
    private CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);

    public List<CategoryResponse> findAll() {

        List<Category> categories = repository.findAll();
        List<CategoryResponse> categoryResponses = categories.stream().map(c -> categoryMapper.toResponse(c))
                .collect(Collectors.toList());

        return categoryResponses;
    }

    public CategoryResponse findById(UUID id) {

        Category category = repository.findById(id).orElse(null);
        CategoryResponse categoryResponse = categoryMapper.toResponse(category);

        return categoryResponse;
    }

    public Category addCategory(CategoryRequest request) {
        var category = categoryMapper.toEntity(request);
        return repository.save(category);
    }

    public void deleteCategory(UUID id) {
        repository.deleteById(id);
    }

    public CategoryResponse addSubcategory(CategoryRequest subcategoryRequest) {
        UUID parentId = subcategoryRequest.getParentId();

        Category subcategory = categoryMapper.toEntity(subcategoryRequest);
        Category parentCategory = repository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid parent category ID"));

        subcategory.setParent(parentCategory);
        parentCategory.addSubcategory(subcategory);

        CategoryResponse cDto = categoryMapper.toResponse(repository.save(parentCategory));

        return cDto;
    }

    public List<UUID> findAllChildCategoryIds(UUID parentId) {
        List<UUID> childCategoryIds = new ArrayList<>();
        List<Category> childCategories = repository.findByParentId(parentId);
        for (Category category : childCategories) {
            childCategoryIds.add(category.getId());
            List<UUID> grandChildCategoryIds = findAllChildCategoryIds(category.getId());
            childCategoryIds.addAll(grandChildCategoryIds);
        }
        return childCategoryIds;
    }
}
