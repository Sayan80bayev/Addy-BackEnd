package com.example.backend.filter.impl;

import com.example.backend.dto.request.AdvertisementFilterRequest;
import com.example.backend.filter.FilterSpecification;
import com.example.backend.model.Advertisement;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceRangeSpecification implements FilterSpecification<Advertisement> {

    @Override
    public Specification<Advertisement> toSpecification(AdvertisementFilterRequest advertisementFilterRequest) {
        return (root, query, criteriaBuilder) -> {
            Double minPrice = advertisementFilterRequest.getMinPrice();
            Double maxPrice = advertisementFilterRequest.getMaxPrice();

            if (minPrice != null && maxPrice != null) {
                return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
            } else if (minPrice != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
            } else if (maxPrice != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
            }
            return null;
        };
    }

    @Override
    public boolean isApplicable(AdvertisementFilterRequest advertisementFilterRequest) {
        return advertisementFilterRequest.getMinPrice() != null || advertisementFilterRequest.getMaxPrice() != null;
    }
}
