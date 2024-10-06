package com.example.backend.controller;

import com.example.backend.dto.request.AdvertisementRequest;
import com.example.backend.dto.response.AdvertisementResponse;
import com.example.backend.service.AdvertisementService;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/advertisements")
public class AdvertisementController {
    @Autowired
    private AdvertisementService service;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> saveAdvertisement(@RequestPart("advertisement") AdvertisementRequest aDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        return ResponseEntity.ok(service.createAdvertisement(aDto, files));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdvertisement(@PathVariable("id") UUID id) {
        service.deleteById(id);
        return ResponseEntity.ok("__SUCCESS__");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editAdvertisement(@PathVariable("id") UUID id,
            @RequestPart("advertisement") AdvertisementRequest aDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        return ResponseEntity.ok(service.updateAdvertisement(aDto, files, id));
    }

    @GetMapping("/getAll")
    public List<AdvertisementResponse> getAdds() {
        return service.findAll();
    }

    @GetMapping("/search/{name}")
    public List<AdvertisementResponse> search(@PathVariable("name") String name) {
        return service.findByName(name);
    }

    @GetMapping("/similars")
    public List<AdvertisementResponse> getSimilars(@RequestParam("cat") UUID catId,
            @RequestParam("price") double price,
            @RequestParam("id") UUID id) {
        return service.findSimilars(catId, price, id);
    }

    @GetMapping("/cat/{id}")
    public List<AdvertisementResponse> getByCat(@PathVariable("id") UUID id) {
        return service.findByCategoryIdOrChildCategoryIds(id);
    }

    @GetMapping("/add/{id}")
    public ResponseEntity<?> getAddById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }
}
