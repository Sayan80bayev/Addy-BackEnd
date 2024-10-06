package com.example.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AdvertisementRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotNull
    private Double price;
    @NotNull
    private UUID categoryId;
}
