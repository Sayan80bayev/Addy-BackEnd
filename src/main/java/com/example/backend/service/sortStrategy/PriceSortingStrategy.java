package com.example.backend.service.sortStrategy;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.example.backend.model.Advertisement;

public class PriceSortingStrategy implements SortingStrategy {
    @Override
    public List<Advertisement> sort(List<Advertisement> ads) {
        return ads.stream()
                .sorted(Comparator.comparing(Advertisement::getPrice))
                .collect(Collectors.toList());
    }
}