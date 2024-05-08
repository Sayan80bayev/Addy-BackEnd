package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.controller.facades.CategoryFacade;
import com.example.backend.dto.*;

@RestController
@RequestMapping("/api/cat")
public class CategoryController {
    @Autowired
    private CategoryFacade facade;

    @PostMapping("/add")
    public ResponseEntity<?> addCategory(@RequestBody CategoryDTO cDto) {
        return facade.addCategory(cDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        return facade.deleteCategory(id);
    }

}
