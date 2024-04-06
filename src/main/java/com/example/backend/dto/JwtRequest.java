package com.example.backend.dto;

import lombok.Data;

@Data
public class JwtRequest {
    private String email;
    private String password;
}
