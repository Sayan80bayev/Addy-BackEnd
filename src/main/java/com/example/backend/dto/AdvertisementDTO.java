package com.example.backend.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.backend.model.Image;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdvertisementDTO {
    private Long id;
    private Double price;
    private String title;
    private String description;
    private Long category_id;
    private Long user_id;
    private Long views;
    private List<Image> images;
}
