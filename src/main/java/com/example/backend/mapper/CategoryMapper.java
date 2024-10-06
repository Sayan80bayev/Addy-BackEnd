package com.example.backend.mapper;

import com.example.backend.dto.request.CategoryRequest;
import com.example.backend.dto.response.CategoryResponse;
import com.example.backend.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import java.util.List;
import java.util.UUID;

@Mapper
// @MapperConfig(componentModel = "spring")
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    // Mapping CategoryRequest to Category Entity
    @Mapping(target = "id", ignore = true) // ID is auto-generated
    @Mapping(target = "subcategories", ignore = true) // Handle subcategories separately if needed
    @Mapping(target = "parent", source = "parentId", qualifiedByName = "mapParentFromId")
    @Mapping(target = "advertisements", ignore = true)
    Category toEntity(CategoryRequest categoryRequest);

    // Mapping Category Entity to CategoryResponse
    @Mapping(target = "categoryId", source = "id") // Map category's id to categoryId in response
    @Mapping(target = "subcategories", source = "subcategories")
    @Mapping(target = "categoryName", source = "name")
    CategoryResponse toResponse(Category category);

    // Utility method to map parent from its ID
    @Named("mapParentFromId")
    default Category mapParentFromId(UUID parentId) {
        if (parentId == null) {
            return null; // Handle null case
        }
        Category parentCategory = new Category();
        parentCategory.setId(parentId);
        return parentCategory;
    }

    // Mapping for a list of Category to a list of CategoryResponse
    List<CategoryResponse> toResponseList(List<Category> categories);
}
