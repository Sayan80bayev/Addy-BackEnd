package com.example.backend.controller;

import java.util.UUID;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.dto.request.UserUpdateRequest;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.service.UserService;

import jakarta.validation.constraints.NotBlank;

@Validated
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping
    public ResponseEntity<?> getUser(@RequestParam @NotBlank String email) {
        return ResponseEntity.ok(service.findByEmail(email));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUser(@RequestParam UUID id) {
        service.deleteUser(id);
        return ResponseEntity.ok("__SUCCESS__");
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestPart("user") UserUpdateRequest request) {
        return ResponseEntity.ok(service.updateUser(request));
    }

    @PutMapping("/avatar/{id}")
    public ResponseEntity<?> updateAvatar(@RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        return ResponseEntity.ok("");
    }

    @GetMapping("/getUserAds/{email}")
    public ResponseEntity<?> getUserAds(@PathVariable("email") String email) {
        return service.getUserAds(email);
    }
}
