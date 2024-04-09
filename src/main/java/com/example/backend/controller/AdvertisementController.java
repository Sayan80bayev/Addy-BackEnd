package com.example.backend.controller;

import com.example.backend.dto.AdvertisementDTO;
import com.example.backend.model.Advertisement;
import com.example.backend.model.User;
import com.example.backend.service.AdvertisementService;
import com.example.backend.service.CategoryService;
import com.example.backend.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> saveAdvertisement(@RequestBody AdvertisementDTO aDto) {
        Advertisement advertisement = Advertisement
                .builder()
                .title(aDto.getTitle())
                .description(aDto.getDescription())
                .category(catService.findById(aDto.getCategory_id()))
                .user(userService.findById(aDto.getUser_id()))
                .date(LocalDateTime.now())
                .views(0L)
                .build();
        try {
            service.save(advertisement);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e + "");
        }
        return ResponseEntity.ok().body("SUCCESS");
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
    public ResponseEntity<?> editAdvertisement(@PathVariable("id") Long id, @RequestBody AdvertisementDTO aDto) {
        Advertisement advertisement = service.findById(id);
        advertisement.setTitle(aDto.getTitle());
        advertisement.setDescription(aDto.getDescription());
        advertisement.setPrice(aDto.getPrice());
        advertisement.setCategory(catService.findById(aDto.getCategory_id()));
        try {
            service.save(advertisement);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e + "");
        }
        return ResponseEntity.ok().body("SUCCESS");
    }
}
