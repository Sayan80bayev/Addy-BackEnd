package com.example.backend.filter.impl;

import com.example.backend.dto.request.AdvertisementFilterRequest;
import com.example.backend.filter.FilterSpecification;
import com.example.backend.model.Advertisement;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CategorySpecification implements FilterSpecification<Advertisement> {
    @Override
    public Specification<Advertisement> toSpecification(AdvertisementFilterRequest advertisementFilterRequest) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("category").get("id"), advertisementFilterRequest.getCategoryId());
    }

    @Override
    public boolean isApplicable(AdvertisementFilterRequest advertisementFilterRequest) {
        return advertisementFilterRequest.getCategoryId() != null;
    }
}
