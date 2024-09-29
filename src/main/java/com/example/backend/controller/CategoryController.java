package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.response.CategoryResponse;
import com.example.backend.service.CategoryService;

@RestController
@RequestMapping("/api/cat")
public class CategoryController {
    @Autowired
    private CategoryService service;

    @PostMapping
    public ResponseEntity<?> addCategory(@RequestBody CategoryResponse cDto) {
        return service.addCategory(cDto);
    }

    @PostMapping("/{parentId}/subcategories")
    public ResponseEntity<?> addSubcategory(@PathVariable Long parentId, @RequestBody CategoryResponse subcategoryDto) {
        return service.addSubcategory(parentId, subcategoryDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        return service.deleteCategory(id);
    }
}
