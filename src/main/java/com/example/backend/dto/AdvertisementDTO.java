package com.example.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdvertisementDTO {
    private Long id;
    private Double price;
    private String title;
    private String description;
    private CategoryDTO category;
    private LocalDateTime date;
    private String email;
    private Long views;
    private List<ImageDTO> images;
}
