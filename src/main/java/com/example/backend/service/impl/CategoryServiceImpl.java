package com.example.backend.service.impl;

import com.example.backend.dto.request.CategoryRequest;
import com.example.backend.dto.response.CategoryResponse;
import com.example.backend.mapper.CategoryMapper;
import com.example.backend.model.Category;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.service.CategoryService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final MessageSource messageSource;
    private final CategoryRepository repository;
    private CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);

    @Override
    public List<CategoryResponse> findAll() {
        List<Category> categories = repository.findAll();
        return categories.stream().map(categoryMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public CategoryResponse findById(UUID id) {
        Category category = repository.findById(id).orElse(null);
        return categoryMapper.toResponse(category);
    }

    @Override
    public Category addCategory(CategoryRequest request) {
        Category category = categoryMapper.toEntity(request);
        category.setId(UUID.randomUUID());
        return repository.save(category);
    }

    @Override
    public void deleteCategory(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public CategoryResponse addSubcategory(CategoryRequest subcategoryRequest) {
        UUID parentId = subcategoryRequest.getParentId();
        Category subcategory = categoryMapper.toEntity(subcategoryRequest);
        Category parentCategory = repository.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        messageSource.getMessage("invalid.parent.category", null, null)));

        subcategory.setParent(parentCategory);
        parentCategory.addSubcategory(subcategory);

        return categoryMapper.toResponse(repository.save(parentCategory));
    }

    @Override
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