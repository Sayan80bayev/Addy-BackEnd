package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepo;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepository;

    public User registerUser(User user) {
        
        return userRepository.save(user);
    }
}
