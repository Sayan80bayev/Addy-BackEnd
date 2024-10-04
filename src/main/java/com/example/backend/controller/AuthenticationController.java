package com.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.request.JwtRequest;
import com.example.backend.dto.request.RegisterRequest;
import com.example.backend.dto.response.JwtResponse;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.service.AuthenticationService;
import com.example.backend.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService service;
    private final UserService uService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {
        UserResponse user = uService.findByEmail(request.getEmail());
        if (user != null) {
            return ResponseEntity.badRequest().body("User already exists");
        }
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JwtResponse> auth(
            @RequestBody JwtRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}
