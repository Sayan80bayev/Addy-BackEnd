package com.example.backend.service;

import com.example.backend.dto.request.UserUpdateRequest;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse findById(UUID id);
    List<UserResponse> findAll();
    UserResponse updateUser(UserUpdateRequest request);
    void updateAvatar(MultipartFile avatar);
    UserResponse findByEmail(String email);
    boolean saveUser(User user);
    void deleteUser(UUID id);
}