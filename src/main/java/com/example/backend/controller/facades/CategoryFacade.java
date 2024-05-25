package com.example.backend.controller.facades;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.model.Category;
import com.example.backend.service.CategoryService;

@Component
public class CategoryFacade {
    @Autowired
    private CategoryService categoryService;

    public ResponseEntity<?> addCategory(CategoryDTO cDto) {
        Category category = categoryService.mapToEntity(cDto);
        Category savedCategory = categoryService.saveCategory(category);
        return ResponseEntity.ok(categoryService.mapToDTO(savedCategory));
    }

    public ResponseEntity<?> addSubcategory(Long parentId, CategoryDTO subcategoryDto) {
        Category subcategory = categoryService.mapToEntity(subcategoryDto);
        categoryService.addSubcategory(parentId, subcategory);
        return ResponseEntity.ok(categoryService.mapToDTO(subcategory));
    }

    public ResponseEntity<?> deleteCategory(Long id) {
        categoryService.delCategory(id);
        return ResponseEntity.ok().build();
    }
}
