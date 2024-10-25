package com.example.backend.service.impl;

import com.example.backend.dto.request.UserUpdateRequest;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.exception.NoChangesException;
import com.example.backend.mapper.UserMapper;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.FileService;
import com.example.backend.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final FileService fileService;
    private final UserRepository repository;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;

    @Override
    public UserResponse findById(UUID id) {
        return repository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(
                        messageSource.getMessage("user.not.found", new Object[]{id}, LocaleContextHolder.getLocale())
                ));
    }

    @Override
    public List<UserResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateUser(UserUpdateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        User user = repository.findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new EntityNotFoundException(
                        messageSource.getMessage("user.not.found", new Object[]{currentUser.getEmail()}, LocaleContextHolder.getLocale())
                ));

        // Validate password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException(
                    messageSource.getMessage("user.password.mismatch", null, LocaleContextHolder.getLocale()));
        }

        // Check for no changes
        boolean isPasswordChanged = !passwordEncoder.matches(request.getPassword(), user.getPassword());
        boolean isUsernameChanged = !user.getName().equals(request.getUsername());

        if (!isPasswordChanged && !isUsernameChanged) {
            throw new NoChangesException(
                    messageSource.getMessage("user.no.changes", null, LocaleContextHolder.getLocale()));
        }

        // Update user details
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getUsername());
        User updatedUser = repository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void updateAvatar(MultipartFile avatar) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        if (user == null) {
            throw new IllegalStateException(
                    messageSource.getMessage("user.authenticated.not.found", null, LocaleContextHolder.getLocale()));
        }

        try {
            String avatarUrl = avatar != null ? fileService.saveFile(avatar) : null;
            user.setAvatarUrl(avatarUrl);
            repository.save(user);
        } catch (Exception e) {
            log.error("Failed to update avatar: {}", e.getMessage(), e);
            throw new RuntimeException(
                    messageSource.getMessage("user.avatar.update.failed", null, LocaleContextHolder.getLocale()), e);
        }
    }

    @Override
    public UserResponse findByEmail(String email) {
        return repository.findByEmail(email)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(
                        messageSource.getMessage("user.not.found", new Object[]{email}, LocaleContextHolder.getLocale())
                ));
    }

    @Override
    public boolean saveUser(User user) {
        try {
            repository.save(user);
            return true;
        } catch (Exception e) {
            log.error("Failed to save user: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void deleteUser(UUID id) {
        repository.deleteById(id);
    }
}