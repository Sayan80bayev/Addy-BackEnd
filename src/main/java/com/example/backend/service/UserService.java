package com.example.backend.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.dto.request.UserUpdateRequest;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.exception.NoChangesException;
import com.example.backend.mapper.UserMapper;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final FileService fileService;
    private final UserRepository repository;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private final PasswordEncoder passwordEncoder;

    public UserResponse findById(UUID id) {

        User user = repository.findById(id).orElse(null);
        UserResponse userResponse = userMapper.toResponse(user);

        return userResponse;
    }

    public List<UserResponse> findAll() {

        List<User> users = repository.findAll();
        List<UserResponse> userResponses = users.stream().map(u -> userMapper.toResponse(u))
                .collect(Collectors.toList());

        return userResponses;
    }

    public UserResponse updateUser(UserUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String password = request.getPassword();
        String confirmPasswrod = request.getConfirmPassword();

        if (!confirmPasswrod.equals(password))
            throw new IllegalArgumentException("Passwords are not equal");

        User userDetails = (User) authentication.getDetails();
        String jwtEmail = userDetails.getEmail();

        User user = repository.findByEmail(jwtEmail)
                .orElseThrow(() -> new EntityNotFoundException("User with email: " + jwtEmail + " not found"));

        password = passwordEncoder.encode(password);

        String existingPassword = user.getPassword();
        String existingName = user.getName();

        if (existingName.equals(request.getUsername()) && existingPassword.equals(password)) {
            throw new NoChangesException("No changes to update");
        }

        user.setPassword(password);
        user.setName(request.getUsername());

        user = repository.save(user);

        return userMapper.toResponse(user);
    }

    public void updateAvatar(MultipartFile avatar) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            throw new IllegalStateException("Authenticated user not found");
        }

        User user = (User) authentication.getPrincipal();

        try {
            String avatarUrl = (avatar != null) ? fileService.saveFile(avatar) : null;
            user.setAvatarUrl(avatarUrl);

            repository.save(user);
        } catch (Exception e) {
            log.error("Failed to update avatar: {}", e.getMessage(), e);
            throw new RuntimeException("Error while updating avatar", e);
        }
    }

    public UserResponse findByEmail(String email) {

        User user = repository.findByEmail(email).orElse(null);
        UserResponse userResponse = userMapper.toResponse(user);

        return userResponse;
    }

    public boolean saveUser(User user) {
        try {
            repository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void deleteUser(UUID id) {
        repository.deleteById(id);
    }

}
