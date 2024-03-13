package com.example.backend.service;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.example.backend.model.Role;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.web.dto.UserRegistrationDto;

@Service
public class UserServiceImpl implements UserService{
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    

    @Override
    public User save(UserRegistrationDto registrationDto) {
        User user = new User(
            registrationDto.getUsername(), 
            registrationDto.getEmail(), 
            registrationDto.getPassword(), Arrays.asList(new Role("ROLE_USER"))); 
        return userRepository.save(user);
    }
    
}
