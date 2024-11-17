package com.example.backend.filter;

import com.example.backend.dto.request.AdvertisementFilterRequest;
import org.springframework.data.jpa.domain.Specification;

public interface FilterSpecification<T> {
    Specification<T> toSpecification(AdvertisementFilterRequest advertisementFilterRequest);
    boolean isApplicable(AdvertisementFilterRequest advertisementFilterRequest);
}
