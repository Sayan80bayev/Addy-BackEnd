package com.example.backend.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.model.Category;
import com.example.backend.repository.CategoryRepository;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository repository;

    public List<Category> findAll() {
        return repository.findAll();
    }

    public Category findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Category saveCategory(Category category) {
        return repository.save(category);
    }

    public void delCategory(Long id) {
        repository.deleteById(id);
    }

    public CategoryDTO mapToDTO(Category category) {
        return mapToDTO(category, new HashSet<>());
    }

    private CategoryDTO mapToDTO(Category category, Set<Category> visited) {
        if (category == null || visited.contains(category)) {
            return null;
        }
        visited.add(category);

        CategoryDTO dto = new CategoryDTO();
        dto.setCategory_id(category.getId());
        dto.setCategory_name(category.getName());
        if (category.getParent() != null) {
            dto.setParent(mapToDTO(category.getParent(), visited));
        }
        dto.setSubcategories(category.getSubcategories().stream()
                .map(subcategory -> mapToDTO(subcategory, visited))
                .collect(Collectors.toList()));
        return dto;
    }

    public Category mapToEntity(CategoryDTO dto) {
        Category category = new Category();
        category.setId(dto.getCategory_id());
        category.setName(dto.getCategory_name());
        if (dto.getParent() != null) {
            category.setParent(mapToEntity(dto.getParent()));
        }
        category.setSubcategories(dto.getSubcategories().stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList()));
        return category;
    }

    public void addSubcategory(Long parentId, Category subcategory) {
        Category parentCategory = repository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid parent category ID"));
        parentCategory.addSubcategory(subcategory);
        repository.save(parentCategory);
    }

    public List<Long> findAllChildCategoryIds(Long parentId) {
        List<Long> childCategoryIds = new ArrayList<>();
        List<Category> childCategories = repository.findByParentId(parentId);
        for (Category category : childCategories) {
            childCategoryIds.add(category.getId());
            List<Long> grandChildCategoryIds = findAllChildCategoryIds(category.getId());
            childCategoryIds.addAll(grandChildCategoryIds);
        }
        return childCategoryIds;
    }
}
