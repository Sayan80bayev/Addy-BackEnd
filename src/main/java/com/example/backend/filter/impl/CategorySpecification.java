package com.example.backend.filter.impl;

import com.example.backend.dto.request.AdvertisementFilterRequest;
import com.example.backend.filter.FilterSpecification;
import com.example.backend.model.Advertisement;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CategorySpecification implements FilterSpecification<Advertisement> {

    @Override
    public Specification<Advertisement> toSpecification(AdvertisementFilterRequest advertisementFilterRequest) {
        return (root, query, criteriaBuilder) -> {
            // Get the category ID from the filter request
            var categoryId = advertisementFilterRequest.getCategoryId();

            // Join the category table
            Join<Object, Object> categoryJoin = root.join("category");

            // Check for matching category ID or its parent's ID
            return criteriaBuilder.or(
                    criteriaBuilder.equal(categoryJoin.get("id"), categoryId),
                    criteriaBuilder.equal(categoryJoin.get("parent").get("id"), categoryId)
            );
        };
    }

    @Override
    public boolean isApplicable(AdvertisementFilterRequest advertisementFilterRequest) {
        return advertisementFilterRequest.getCategoryId() != null;
    }
}