package com.example.backend.dto;

import lombok.Data;

@Data
public class ImageDTO {
    private Long id;
    private byte[] imageData;
}
