package com.example.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.dto.CategoryDTO;
import com.example.backend.model.Category;
import com.example.backend.repository.CategoryRepository;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository repository;

    public List<Category> findAll() {
        return repository.findAll();
    }

    public Category findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Category saveCategory(Category category) {
        return repository.save(category);
    }

    public void delCategory(Long id) {
        repository.deleteById(id);
    }

    public CategoryDTO mapToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategory_id(category.getId());
        dto.setCategory_name(category.getName());
        return dto;
    }

    public Category mapToEntity(CategoryDTO dto) {
        Category category = new Category();
        category.setId(dto.getCategory_id());
        category.setName(dto.getCategory_name());
        return category;
    }
}
