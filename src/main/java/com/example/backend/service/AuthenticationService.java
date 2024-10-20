package com.example.backend.service;

import com.example.backend.dto.request.JwtRequest;
import com.example.backend.dto.request.RegisterRequest;
import com.example.backend.dto.response.JwtResponse;

public interface AuthenticationService {
    JwtResponse register(RegisterRequest request);

    JwtResponse authenticate(JwtRequest request);
}