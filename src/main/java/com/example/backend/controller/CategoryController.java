package com.example.backend.controller;

import java.util.UUID;

import com.example.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.request.CategoryRequest;
import com.example.backend.service.impl.CategoryServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService service;

    @Operation(summary = "Get all categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<?> allCategories() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Get a category by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> categoryById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Add a new category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<?> addCategory(@RequestBody CategoryRequest cDto) {
        return ResponseEntity.ok(service.addCategory(cDto));
    }

    @Operation(summary = "Add a subcategory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Subcategory added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/subcategory")
    public ResponseEntity<?> addSubcategory(@RequestBody CategoryRequest subcategoryDto) {
        return ResponseEntity.ok(service.addSubcategory(subcategoryDto));
    }

    @Operation(summary = "Delete a category by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable UUID id) {
        service.deleteCategory(id);
        return ResponseEntity.ok("__SUCCESS__");
    }
}
