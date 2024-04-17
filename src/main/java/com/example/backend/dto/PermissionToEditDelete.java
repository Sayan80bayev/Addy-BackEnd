package com.example.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionToEditDelete {
    private AdvertisementDTO aDto;
    private String permission;
}
