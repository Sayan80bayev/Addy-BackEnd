package com.example.backend.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdvertisementResponse {
    private UUID id;
    private Double price;
    private String title;
    private String description;
    private CategoryResponse category;
    private LocalDateTime date;
    private String email;
    private Long views;
    private List<String> imagesUrl;
}
