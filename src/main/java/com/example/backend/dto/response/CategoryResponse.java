package com.example.backend.dto.response;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private UUID categoryId;
    private String categoryName;
    private List<CategoryResponse> subcategories;
    private CategoryResponse parent;
}
