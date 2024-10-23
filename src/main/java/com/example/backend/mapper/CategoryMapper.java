package com.example.backend.mapper;

import com.example.backend.dto.request.CategoryRequest;
import com.example.backend.dto.response.CategoryResponse;
import com.example.backend.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    // Mapping CategoryRequest to Category Entity
    @Mapping(target = "id", ignore = true) // ID is auto-generated
    @Mapping(target = "subcategories", ignore = true) // Handle subcategories separately if needed
    @Mapping(target = "parent", source = "parentId", qualifiedByName = "mapParentFromId")
    @Mapping(target = "advertisements", ignore = true)
    Category toEntity(CategoryRequest categoryRequest);

    // Mapping Category Entity to CategoryResponse (excluding subcategories to avoid cycles)
    @Mapping(target = "categoryId", source = "id")
    @Mapping(target = "subcategories", source = "subcategories", qualifiedByName = "mapSubcategoriesToResponse")
    @Mapping(target = "categoryName", source = "name")
    CategoryResponse toResponse(Category category);

    // Utility method to map parent from its ID
    @Named("mapParentFromId")
    default Category mapParentFromId(UUID parentId) {
        if (parentId == null) {
            return null;
        }
        Category parentCategory = new Category();
        parentCategory.setId(parentId);
        return parentCategory;
    }

    // Prevent mapping subcategories recursively by limiting the depth
    @Named("mapSubcategoriesToResponse")
    default List<CategoryResponse> mapSubcategoriesToResponse(List<Category> subcategories) {
        if (subcategories == null) {
            return null;
        }
        return subcategories.stream()
                .map(subcategory -> CategoryResponse.builder()
                        .categoryId(subcategory.getId())
                        .categoryName(subcategory.getName())
                        // You can choose to omit the parent or other fields here if not needed
                        .build())
                .collect(Collectors.toList());
    }

    // Mapping for a list of Category to a list of CategoryResponse
    List<CategoryResponse> toResponseList(List<Category> categories);
}
