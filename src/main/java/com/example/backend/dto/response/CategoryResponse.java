package com.example.backend.dto.response;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CategoryResponse {
    private UUID categoryId;
    private String categoryName;
    private List<CategoryResponse> subcategories;
    private CategoryResponse parent;
}
