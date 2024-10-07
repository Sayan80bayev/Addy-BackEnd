package com.example.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CategoryRequest {
    @NotBlank(message = "{category.name.notBlank}")
    private String name;
    private UUID parentId;
}
