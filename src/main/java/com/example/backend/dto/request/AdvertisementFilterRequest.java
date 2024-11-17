package com.example.backend.dto.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AdvertisementFilterRequest {
    private String title;
    private UUID categoryId;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
    private Double minPrice;
    private Double maxPrice;
}
