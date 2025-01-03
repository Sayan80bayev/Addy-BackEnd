package com.example.backend.repository;

import com.example.backend.model.Advertisement;
import com.example.backend.model.Category;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, UUID> {
    List<Advertisement> findAll(Specification<Advertisement> spec);
    @Query("SELECT a FROM Advertisement a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Advertisement> findByTitle(String title);

    @Query("SELECT a FROM Advertisement a WHERE a.price <= :price * 2 AND a.category = :cat AND a.id != :id")
    List<Advertisement> findSimilars(Category cat, Double price, UUID id);

    List<Advertisement> findByCategoryId(UUID id);

    List<Advertisement> findByCategoryIdOrCategoryParentId(UUID categoryId, UUID parentCategoryId);

    List<Advertisement> findByCategoryIdIn(List<UUID> categoryIds);

}
