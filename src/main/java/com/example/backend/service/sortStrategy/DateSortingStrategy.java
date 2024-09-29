package com.example.backend.service.sortStrategy;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.example.backend.dto.response.AdvertisementResponse;

public class DateSortingStrategy implements SortingStrategy {
    @Override
    public List<AdvertisementResponse> sort(List<AdvertisementResponse> ads) {
        return ads.stream()
                .sorted(Comparator.comparing(AdvertisementResponse::getDate))
                .collect(Collectors.toList());
    }
}