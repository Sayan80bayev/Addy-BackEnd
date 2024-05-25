package com.example.backend.repository;

import com.example.backend.model.Advertisement;
import com.example.backend.model.Category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
    @Query("SELECT a FROM Advertisement a WHERE a.title LIKE :title%")
    List<Advertisement> findByTitle(String title);

    @Query("SELECT a FROM Advertisement a WHERE a.price <= :price * 2 AND a.category = :cat AND a.id != :id")
    List<Advertisement> findSimilars(Category cat, double price, Long id);

    List<Advertisement> findByCategoryId(Long id);

    List<Advertisement> findByCategoryIdOrCategoryParentId(Long categoryId, Long parentCategoryId);

    List<Advertisement> findByCategoryIdIn(List<Long> categoryIds);

}
