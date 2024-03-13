package com.example.backend.service;

import org.springframework.stereotype.Service;

import com.example.backend.model.User;
import com.example.backend.web.dto.UserRegistrationDto;

@Service
public interface UserService {
    User save(UserRegistrationDto registrationDto);
}
