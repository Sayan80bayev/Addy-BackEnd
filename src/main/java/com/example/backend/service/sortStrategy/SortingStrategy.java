package com.example.backend.service.sortStrategy;

import java.util.List;

import com.example.backend.dto.response.AdvertisementResponse;

public interface SortingStrategy {
    List<AdvertisementResponse> sort(List<AdvertisementResponse> ads);
}