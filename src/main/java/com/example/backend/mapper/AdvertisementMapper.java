package com.example.backend.mapper;

import com.example.backend.dto.request.AdvertisementRequest;
import com.example.backend.dto.response.AdvertisementResponse;
import com.example.backend.model.Advertisement;
import com.example.backend.model.Category;
import com.example.backend.model.User;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AdvertisementMapper {

    AdvertisementMapper INSTANCE = Mappers.getMapper(AdvertisementMapper.class);

    // Mapping AdvertisementRequest to Advertisement Entity
    @Mapping(target = "id", ignore = true) // ID is auto-generated
    @Mapping(target = "date", expression = "java(java.time.LocalDateTime.now())") // Set current time for date
    @Mapping(target = "views", constant = "0L") // Default views to 0
    @Mapping(target = "user", source = "userId", qualifiedByName = "mapUserFromId")
    @Mapping(target = "category", source = "categoryId", qualifiedByName = "mapCategoryFromId")
    Advertisement toEntity(AdvertisementRequest advertisementRequest);

    // Mapping Advertisement Entity to AdvertisementResponse
    @Mapping(target = "id", source = "advertisement.id")
    @Mapping(target = "email", source = "advertisement.user.email")
    @Mapping(target = "category", source = "advertisement.category")
    @Mapping(target = "imagesUrl", source = "advertisement.image")
    AdvertisementResponse toResponse(Advertisement advertisement);

    // Utility methods to map user and category from their IDs
    @Named("mapUserFromId")
    default User mapUserFromId(UUID userId) {
        User user = new User();
        user.setId(userId);
        return user;
    }

    @Named("mapCategoryFromId")
    default Category mapCategoryFromId(UUID categoryId) {
        Category category = new Category();
        category.setId(categoryId);
        return category;
    }
}
