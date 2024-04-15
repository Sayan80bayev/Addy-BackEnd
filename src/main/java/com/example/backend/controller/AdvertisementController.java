package com.example.backend.controller;

import com.example.backend.dto.AdvertisementDTO;
import com.example.backend.model.Advertisement;
import com.example.backend.model.User;
import com.example.backend.service.AdvertisementService;
import com.example.backend.service.CategoryService;
import com.example.backend.service.UserService;

import io.jsonwebtoken.io.IOException;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
// @CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/secured")
public class AdvertisementController {
    @Autowired
    private AdvertisementService service;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService catService;

    @PostMapping("/create")
    public ResponseEntity<?> saveAdvertisement(@RequestPart("advertisement") AdvertisementDTO aDto,
            @RequestPart("files") List<MultipartFile> files) {
        Advertisement advertisement = Advertisement.builder()
                .title(aDto.getTitle())
                .description(aDto.getDescription())
                .category(catService.findById(aDto.getCategory_id()))
                .user(userService.findById(aDto.getUser_id()))
                .price(aDto.getPrice())
                .date(LocalDateTime.now())
                .views(0L)
                .build();

        // Handle file uploads
        for (MultipartFile file : files) {
            try {
                // Process each file, for example, save to a location
                // You may want to set up a service for handling file storage
                // file.transferTo(new File("/path/to/uploaded/files/" +
                // file.getOriginalFilename()));
            } catch (IOException e) {
                // Handle the exception
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("File upload failed: " + e.getMessage());
            }
        }

        advertisement.setImages(files); // Assuming you have a method in Advertisement to set images

        try {
            service.save(advertisement);
            return ResponseEntity.ok().body("SUCCESS");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Advertisement creation failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAdvertisement(@PathVariable("id") Long id) {
        try {
            service.deleteById(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e + "");
        }
        return ResponseEntity.ok().body("SUCCESS");
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editAdvertisement(@PathVariable("id") Long id,
            @RequestPart("advertisement") AdvertisementDTO aDto,
            @RequestPart("files") List<MultipartFile> files) {
        Advertisement advertisement = service.findById(id);
        advertisement.setTitle(aDto.getTitle());
        advertisement.setDescription(aDto.getDescription());
        advertisement.setPrice(aDto.getPrice());
        advertisement.setCategory(catService.findById(aDto.getCategory_id()));
        if (files != null) {
            advertisement.setImages(files);
        }
        advertisement.setImages(null);
        try {
            service.save(advertisement);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e + "");
        }
        return ResponseEntity.ok().body("SUCCESS");
    }
}
