package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {

}
