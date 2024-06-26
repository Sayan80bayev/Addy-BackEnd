package com.example.backend.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CategoryDTO {
    private Long category_id;
    private String category_name;
    private List<CategoryDTO> subcategories = new ArrayList<>();
    private CategoryDTO parent;
}
