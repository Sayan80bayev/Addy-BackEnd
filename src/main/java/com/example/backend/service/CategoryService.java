package com.example.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.model.Category;
import com.example.backend.repository.CategoryRepository;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository repository;

    public List<Category> findAll() {
        return repository.findAll();
    }

    public Category findById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public Category saveCategory(Category category) {
        return repository.save(category);
    }

    public void delCategory(UUID id) {
        repository.deleteById(id);
    }

    public void addSubcategory(UUID parentId, Category subcategory) {
        Category parentCategory = repository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid parent category ID"));
        parentCategory.addSubcategory(subcategory);
        repository.save(parentCategory);
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
