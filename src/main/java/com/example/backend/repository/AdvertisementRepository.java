package com.example.backend.repository;

import com.example.backend.model.Advertisement;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
    @Query("SELECT a FROM Advertisement a WHERE a.title LIKE :title%")
    List<Advertisement> findByTitle(String title);

    List<Advertisement> findByCategoryId(Long id);
}
