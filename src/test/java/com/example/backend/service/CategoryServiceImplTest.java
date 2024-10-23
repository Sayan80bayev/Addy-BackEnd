package com.example.backend.service;

import com.example.backend.dto.request.CategoryRequest;
import com.example.backend.dto.response.CategoryResponse;
import com.example.backend.mapper.CategoryMapper;
import com.example.backend.model.Category;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.service.impl.CategoryServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository repository;

    @Mock
    private MessageSource messageSource;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test case: Successfully find all categories
    @Test
    void testFindAllCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category());
        categories.add(new Category());

        when(repository.findAll()).thenReturn(categories);

        List<CategoryResponse> responses = categoryService.findAll();

        assertEquals(2, responses.size());
        verify(repository, times(1)).findAll();
    }

    // Test case: Find category by ID (Success)
    @Test
    void testFindByIdSuccess() {
        UUID id = UUID.randomUUID();
        Category category = new Category();
        category.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(category));

        CategoryResponse response = categoryService.findById(id);

        assertNotNull(response);
        assertEquals(id, response.getCategoryId());
        verify(repository, times(1)).findById(id);
    }

    // Test case: Find category by ID (Not found)
    @Test
    void testFindByIdNotFound() {
        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        CategoryResponse response = categoryService.findById(id);

        assertNull(response);
        verify(repository, times(1)).findById(id);
    }

    // Test case: Add category (Success)
    @Test
    void testAddCategorySuccess() {
        CategoryRequest request = new CategoryRequest();
        request.setName("New Category");

        Category category = new Category();
        category.setName("New Category");

        when(repository.save(any(Category.class))).thenReturn(category);

        Category addedCategory = categoryService.addCategory(request);

        assertNotNull(addedCategory);
        assertEquals("New Category", addedCategory.getName());
        verify(repository, times(1)).save(any(Category.class));
    }

    // Test case: Delete category by ID (Success)
    @Test
    void testDeleteCategorySuccess() {
        UUID id = UUID.randomUUID();

        doNothing().when(repository).deleteById(id);

        categoryService.deleteCategory(id);

        verify(repository, times(1)).deleteById(id);
    }

    // Test case: Add subcategory (Success)
    @Test
    void testAddSubcategorySuccess() {
        UUID parentId = UUID.randomUUID();
        CategoryRequest subcategoryRequest = new CategoryRequest();
        subcategoryRequest.setParentId(parentId);
        subcategoryRequest.setName("Subcategory");

        Category parentCategory = new Category();
        parentCategory.setId(parentId);
        parentCategory.setName("Parent Category");
        parentCategory.setSubcategories(new ArrayList<>());

        Category subcategory = new Category();
        subcategory.setName("Subcategory");

        // Map the request to the subcategory entity
        when(categoryMapper.toEntity(subcategoryRequest)).thenReturn(subcategory);
        // Find the parent category by its ID
        when(repository.findById(parentId)).thenReturn(Optional.of(parentCategory));
        // Save the parent category after adding the subcategory
        when(repository.save(parentCategory)).thenReturn(parentCategory);

        // Act
        CategoryResponse response = categoryService.addSubcategory(subcategoryRequest);

        // Assert that the subcategory was added correctly
        assertNotNull(response);
        assertEquals("Parent Category", response.getCategoryName());  // Correct assertion: checking the subcategory name
        verify(repository, times(1)).findById(parentId);
        verify(repository, times(1)).save(parentCategory);
    }

    // Test case: Add subcategory (Parent category not found)
    @Test
    void testAddSubcategoryParentNotFound() {
        UUID parentId = UUID.randomUUID();
        CategoryRequest subcategoryRequest = new CategoryRequest();
        subcategoryRequest.setParentId(parentId);

        when(repository.findById(parentId)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("invalid.parent.category"), any(), any()))
                .thenReturn("Parent category not found");

        assertThrows(EntityNotFoundException.class, () -> categoryService.addSubcategory(subcategoryRequest));

        verify(repository, times(1)).findById(parentId);
    }

    // Test case: Find all child category IDs (No children)
    @Test
    void testFindAllChildCategoryIdsNoChildren() {
        UUID parentId = UUID.randomUUID();

        when(repository.findByParentId(parentId)).thenReturn(new ArrayList<>());

        List<UUID> childIds = categoryService.findAllChildCategoryIds(parentId);

        assertTrue(childIds.isEmpty());
        verify(repository, times(1)).findByParentId(parentId);
    }

    // Test case: Find all child category IDs (With children and grandchildren)
    @Test
    void testFindAllChildCategoryIdsWithChildren() {
        UUID parentId = UUID.randomUUID();
        UUID childId1 = UUID.randomUUID();
        UUID childId2 = UUID.randomUUID();
        UUID grandChildId = UUID.randomUUID();

        Category child1 = new Category();
        child1.setId(childId1);
        Category child2 = new Category();
        child2.setId(childId2);

        Category grandChild = new Category();
        grandChild.setId(grandChildId);

        List<Category> children = List.of(child1, child2);
        List<Category> grandChildren = List.of(grandChild);

        when(repository.findByParentId(parentId)).thenReturn(children);
        when(repository.findByParentId(childId1)).thenReturn(grandChildren);
        when(repository.findByParentId(childId2)).thenReturn(new ArrayList<>());

        List<UUID> childCategoryIds = categoryService.findAllChildCategoryIds(parentId);

        assertEquals(3, childCategoryIds.size());
        assertTrue(childCategoryIds.contains(childId1));
        assertTrue(childCategoryIds.contains(childId2));
        assertTrue(childCategoryIds.contains(grandChildId));

        verify(repository, times(1)).findByParentId(parentId);
        verify(repository, times(1)).findByParentId(childId1);
        verify(repository, times(1)).findByParentId(childId2);
    }
}
