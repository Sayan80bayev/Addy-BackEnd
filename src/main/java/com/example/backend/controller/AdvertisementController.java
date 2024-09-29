package com.example.backend.controller;

import com.example.backend.controller.facades.AdvertisementFacade;
import com.example.backend.dto.response.AdvertisementResponse;
import com.example.backend.service.AdvertisementService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/secured")
public class AdvertisementController {
    @Autowired
    private AdvertisementService service;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> saveAdvertisement(@RequestPart("advertisement") AdvertisementResponse aDto,
            @RequestPart("files") List<MultipartFile> files) {
        return service.saveAdvertisement(aDto, files);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdvertisement(@PathVariable("id") Long id) {
        return service.deleteAdvertisement(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editAdvertisement(@PathVariable("id") Long id,
            @RequestPart("advertisement") AdvertisementResponse aDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        return service.editAdvertisement(id, aDto, files);
    }
}
