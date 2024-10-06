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
import org.springframework.stereotype.Component;

@Component
@Mapper(uses = CategoryMapper.class)
public interface AdvertisementMapper {

    AdvertisementMapper INSTANCE = Mappers.getMapper(AdvertisementMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "subscriptions", ignore = true)
    @Mapping(target = "imagesUrl", ignore = true)
    @Mapping(target = "views", constant = "0L")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    Advertisement toEntity(AdvertisementRequest advertisementRequest);

    @Mapping(target = "id", source = "advertisement.id")
    @Mapping(target = "email", source = "advertisement.user.email")
    @Mapping(target = "category", source = "advertisement.category")
    @Mapping(target = "imagesUrl", source = "advertisement.imagesUrl")
    AdvertisementResponse toResponse(Advertisement advertisement);

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
