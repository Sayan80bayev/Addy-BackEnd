package com.example.backend.service.sortStrategy;

import java.util.List;

import com.example.backend.model.Advertisement;

public interface SortingStrategy {
    List<Advertisement> sort(List<Advertisement> ads);
}