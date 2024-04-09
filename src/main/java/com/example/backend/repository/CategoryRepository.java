package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
