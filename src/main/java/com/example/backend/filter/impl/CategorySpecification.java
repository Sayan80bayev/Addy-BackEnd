package com.example.backend.filter.impl;

import com.example.backend.dto.request.AdvertisementFilterRequest;
import com.example.backend.filter.FilterSpecification;
import com.example.backend.model.Advertisement;
import com.example.backend.model.Category;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.utils.CategoryUtils;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class CategorySpecification implements FilterSpecification<Advertisement> {

    private final CategoryRepository categoryRepository;

    public CategorySpecification(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Specification<Advertisement> toSpecification(AdvertisementFilterRequest request) {
        return (root, query, criteriaBuilder) -> {
            Category parentCategory = categoryRepository.findById(request.getCategoryId()).orElseThrow();
            List<UUID> descendantIds = new ArrayList<>();
            CategoryUtils.findAllDescendants(parentCategory, descendantIds);
            return root.get("category").get("id").in(descendantIds);
        };
    }

    @Override
    public boolean isApplicable(AdvertisementFilterRequest request) {
        return request.getCategoryId() != null;
    }
}