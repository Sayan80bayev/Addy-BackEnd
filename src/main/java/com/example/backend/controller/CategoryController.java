package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.backend.controller.facades.CategoryFacade;
import com.example.backend.dto.CategoryDTO;

@RestController
@RequestMapping("/api/cat")
public class CategoryController {
    @Autowired
    private CategoryFacade facade;

    @PostMapping("/add")
    public ResponseEntity<?> addCategory(@RequestBody CategoryDTO cDto) {
        return facade.addCategory(cDto);
    }

    @PostMapping("/{parentId}/subcategories")
    public ResponseEntity<?> addSubcategory(@PathVariable Long parentId, @RequestBody CategoryDTO subcategoryDto) {
        return facade.addSubcategory(parentId, subcategoryDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        return facade.deleteCategory(id);
    }
}
