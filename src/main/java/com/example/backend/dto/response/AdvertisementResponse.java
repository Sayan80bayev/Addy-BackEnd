package com.example.backend.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdvertisementResponse {
    private Long id;
    private Double price;
    private String title;
    private String description;
    private CategoryResponse category;
    private LocalDateTime date;
    private String email;
    private Long views;
    private List<String> imagesUrl;
}
