package com.example.backend.controller;

import com.example.backend.controller.facades.AdvertisementFacade;
import com.example.backend.dto.AdvertisementDTO;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/secured")
public class AdvertisementController {
    @Autowired
    private AdvertisementFacade facade;

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<?> saveAdvertisement(@RequestPart("advertisement") AdvertisementDTO aDto,
            @RequestPart("files") List<MultipartFile> files) {
        return facade.saveAdvertisement(aDto, files);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAdvertisement(@PathVariable("id") Long id) {
        return facade.deleteAdvertisement(id);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> editAdvertisement(@PathVariable("id") Long id,
            @RequestPart("advertisement") AdvertisementDTO aDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        return facade.editAdvertisement(id, aDto, files);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody String email) {
        return ResponseEntity.ok().body(null);
    }
}
