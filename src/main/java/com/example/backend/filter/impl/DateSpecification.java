package com.example.backend.filter.impl;

import com.example.backend.dto.request.AdvertisementFilterRequest;
import com.example.backend.filter.FilterSpecification;
import com.example.backend.model.Advertisement;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DateSpecification implements FilterSpecification<Advertisement> {

    @Override
    public Specification<Advertisement> toSpecification(AdvertisementFilterRequest advertisementFilterRequest) {
        return (root, query, criteriaBuilder) -> {
            LocalDateTime createdAfter = advertisementFilterRequest.getCreatedAfter();
            LocalDateTime createdBefore = advertisementFilterRequest.getCreatedBefore();

            if (createdAfter != null && createdBefore != null) {
                return criteriaBuilder.between(root.get("date"), createdAfter, createdBefore);
            } else if (createdAfter != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("date"), createdAfter);
            } else if (createdBefore != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("date"), createdBefore);
            }
            return null; // No filtering condition if both dates are null
        };
    }

    @Override
    public boolean isApplicable(AdvertisementFilterRequest advertisementFilterRequest) {
        return advertisementFilterRequest.getCreatedAfter() != null
                || advertisementFilterRequest.getCreatedBefore() != null;
    }
}