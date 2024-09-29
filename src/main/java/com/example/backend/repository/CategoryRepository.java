package com.example.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.Category;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    List<Category> findByParentId(UUID parentId);
}
