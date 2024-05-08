package com.example.backend.controller.facades;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import com.example.backend.dto.CategoryDTO;
import com.example.backend.model.Category;
import com.example.backend.service.CategoryService;

@Component
public class CategoryFacade {
    @Autowired
    private CategoryService service;

    public ResponseEntity<?> addCategory(CategoryDTO cDto) {
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

    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
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
