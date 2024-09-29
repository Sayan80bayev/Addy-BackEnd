package com.example.backend.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class CategoryResponse {
    private Long categoryId;
    private String categoryName;
    private List<CategoryResponse> subcategories;
    private CategoryResponse parent;
}
