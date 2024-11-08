package com.example.backend.service;

import com.example.backend.dto.request.UserUpdateRequest;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse findById(UUID id);
    UserResponse findByEmail(String email);
    UserResponse updateUser(UserUpdateRequest request);

    List<UserResponse> findAll();

    void updateAvatar(MultipartFile avatar);
    void deleteUser(UUID id);

    boolean saveUser(User user);
}