package com.example.backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AdvertisementRequest {
    @NotBlank(message = "{advertisement.title.notBlank}")
    private String title;

    private String description;

    @NotNull(message = "{advertisement.price.notNull}")
    @Min(value = 0, message = "{advertisement.price.min}")
    private Double price;

    @NotNull(message = "{advertisement.categoryId.notNull}")
    private UUID categoryId;
}
