package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.Category;
import com.example.backend.dto.*;
import com.example.backend.service.CategoryService;

@RestController
@RequestMapping("/api/cat")
public class CategoryController {
    private final CategoryService service;

    @Autowired
    public CategoryController(CategoryService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCategory(@RequestBody CategoryDTO cDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            Category category = service.mapToEntity(cDto);
            try {
                service.saveCategory(category);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("" + e);
            }
            return ResponseEntity.ok().body("SUCCESS");
        }
        return ResponseEntity.badRequest().body("NO_ACCESS");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> addCategory(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            try {
                service.delCategory(id);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("" + e);
            }
            return ResponseEntity.ok().body("SUCCESS");
        }
        return ResponseEntity.badRequest().body("NO_ACCESS");

    }

}
