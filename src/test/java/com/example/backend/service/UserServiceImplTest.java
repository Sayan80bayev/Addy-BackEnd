package com.example.backend.service;

import com.example.backend.dto.request.UserUpdateRequest;
import com.example.backend.dto.response.UserResponse;
import com.example.backend.exception.NoChangesException;
import com.example.backend.mapper.UserMapper;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.FileService;
import com.example.backend.service.impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private FileService fileService;
    @Mock
    private UserRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MessageSource messageSource;
    @Mock
    private MultipartFile avatar;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(fileService, repository, passwordEncoder, messageSource);
        UUID userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("encodedPassword");

        // Set up security context mock
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
    }

    @Test
    void updateUser_ShouldUpdateUserDetails_WhenValidRequest() {
        UserUpdateRequest request = UserUpdateRequest.builder()
                .username("newUsername")
                .password("newPassword")
                .confirmPassword("newPassword")
                .build();        when(repository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedNewPassword");
        when(repository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.updateUser(request);

        assertNotNull(response);
        assertEquals("newUsername", user.getName());
        assertEquals("encodedNewPassword", user.getPassword());
        verify(repository).save(user);
    }

    @Test
    void updateUser_ShouldThrowNoChangesException_WhenNoChangesMade() {
        UserUpdateRequest request = UserUpdateRequest.builder()
                .username("Test User")
                .password("encodedPassword")
                .confirmPassword("encodedPassword")
                .build();        when(repository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);

        assertThrows(NoChangesException.class, () -> userService.updateUser(request));
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void updateUser_ShouldThrowIllegalArgumentException_WhenPasswordsDoNotMatch() {
        UserUpdateRequest request = UserUpdateRequest.builder()
                .username("newUsername")
                .password("newPassword")
                .confirmPassword("differentPassword")
                .build();

        when(repository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(request));
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void updateAvatar_ShouldUpdateAvatar_WhenValidAvatarProvided() throws Exception {
        when(fileService.saveFile(avatar)).thenReturn("avatarUrl");
        when(repository.save(user)).thenReturn(user);

        userService.updateAvatar(avatar);

        assertEquals("avatarUrl", user.getAvatarUrl());
        verify(repository).save(user);
    }

    @Test
    void updateAvatar_ShouldThrowIllegalStateException_WhenUserNotAuthenticated() {
        when(authentication.getPrincipal()).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> userService.updateAvatar(avatar));
        verify(repository, never()).save(any(User.class));
    }

    @Test
    void updateAvatar_ShouldHandleFileServiceException() throws Exception {
        when(fileService.saveFile(avatar)).thenThrow(new RuntimeException("File save failed"));
        when(messageSource.getMessage(any(), any(), any())).thenReturn("File save failed");

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> userService.updateAvatar(avatar));
        assertEquals("File save failed", thrown.getMessage());
        verify(repository, never()).save(any(User.class));
    }
}
