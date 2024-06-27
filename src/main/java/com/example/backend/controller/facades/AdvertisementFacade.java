package com.example.backend.controller.facades;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.dto.AdvertisementDTO;
import com.example.backend.model.Advertisement;
import com.example.backend.model.User;
import com.example.backend.service.AdvertisementService;
import com.example.backend.service.CategoryService;
import com.example.backend.service.ImageService;
import com.example.backend.service.UserService;

@Component
public class AdvertisementFacade {
    @Autowired
    private UserService userService;
    @Autowired
    private AdvertisementService service;
    @Autowired
    private CategoryService catService;
    @Autowired
    private ImageService iService;

    public ResponseEntity<?> saveAdvertisement(AdvertisementDTO aDto,
            List<MultipartFile> files) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Advertisement advertisement = Advertisement.builder()
                .title(aDto.getTitle())
                .description(aDto.getDescription())
                .category(catService.findById(aDto.getCategory().getCategory_id()))
                .user(currentUser)
                .price(aDto.getPrice())
                .date(LocalDateTime.now())
                .views(0L)
                .build();
        String result = iService.validateAndSaveImages(files);
        if (result != null)
            return ResponseEntity.badRequest().body(result);
        advertisement.setImages(files);

        try {
            service.save(advertisement);
<<<<<<< HEAD
            return ResponseEntity.ok().body("{\"status\":\"SUCCESS\"}");
=======
            return ResponseEntity.ok().body("SUCCESS");
>>>>>>> 1d22e87bdf99d1899bfabbb55cef854843513403
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Advertisement creation failed: " + e.getMessage());
        }
    }

    public ResponseEntity<?> deleteAdvertisement(Long id) {
        Advertisement advertisement = service.findById(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        if (!advertisement.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You don't have permission to edit this advertisement.");
        }
        try {
            service.deleteById(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e + "");
        }
<<<<<<< HEAD
        return ResponseEntity.ok().body("{\"status\":\"SUCCESS\"}");
=======
        return ResponseEntity.ok().body("SUCCESS");
>>>>>>> 1d22e87bdf99d1899bfabbb55cef854843513403
    }

    public ResponseEntity<?> editAdvertisement(Long id,
            AdvertisementDTO aDto,
            List<MultipartFile> files) {
        Advertisement advertisement = service.findById(id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (!advertisement.getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You don't have permission to edit this advertisement.");
        }

        advertisement.setTitle(aDto.getTitle());
        advertisement.setDescription(aDto.getDescription());
        advertisement.setPrice(aDto.getPrice());
        advertisement.setCategory(catService.findById(aDto.getCategory().getCategory_id()));
        advertisement.setImages(files);
        if (files == null) {
            advertisement.setImages(null);
        } else {
            String result = iService.validateAndSaveImages(files);
            if (result != null) {
                return ResponseEntity.badRequest().body(result);
            }
        }

        try {
            service.save(advertisement);
            service.update(advertisement);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e + "");
        }
<<<<<<< HEAD
        return ResponseEntity.ok().body("{\"status\":\"SUCCESS\"}");
=======
        return ResponseEntity.ok().body("SUCCESS");
>>>>>>> 1d22e87bdf99d1899bfabbb55cef854843513403
    }
}
