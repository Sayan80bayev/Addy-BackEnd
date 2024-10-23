package com.example.backend.service;

import com.example.backend.config.JwtService;
import com.example.backend.dto.request.JwtRequest;
import com.example.backend.dto.request.RegisterRequest;
import com.example.backend.dto.response.JwtResponse;
import com.example.backend.enums.Role;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AuthenticationServiceImplTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test case: Successful authentication
    @Test
    void testAuthenticateSuccess() {
        JwtRequest request = JwtRequest.builder()
                .email("test@example.com")
                .password("Password123!")
                .build();

        User user = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwtToken");

        JwtResponse response = authenticationService.authenticate(request);

        assertEquals("jwtToken", response.getToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken(user);
    }

    // Test case: User not found during authentication
    @Test
    void testAuthenticateUserNotFound() {
        JwtRequest request = JwtRequest.builder()
                .email("invalid@example.com")
                .password("Password123!")
                .build();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authenticationService.authenticate(request));
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail(request.getEmail());
    }

    // Test case: Password mismatch during authentication
    @Test
    void testAuthenticatePasswordMismatch() {
        // Arrange
        JwtRequest request = JwtRequest.builder()
                .email("test@example.com")
                .password("wrongPassword")
                .build();

        // Simulate that authentication fails due to password mismatch
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Passwords do not match"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authenticationService.authenticate(request));

        // Verify that authenticate was called, but findByEmail was not
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByEmail(request.getEmail());
    }

    // Test case: Successful registration
    @Test
    void testRegisterSuccess() {
        RegisterRequest request = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("Password123!")
                .build();

        User user = User.builder()
                .id(UUID.randomUUID())
                .name(request.getUsername())
                .email(request.getEmail())
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        JwtResponse response = authenticationService.register(request);

        assertEquals("jwtToken", response.getToken());
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtService, times(1)).generateToken(any(User.class));
    }

    // Test case: Null request during authentication
    @Test
    void testAuthenticateNullRequest() {
        assertThrows(NullPointerException.class, () -> authenticationService.authenticate(null));
        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByEmail(anyString());
    }

    // Test case: Null request during registration
    @Test
    void testRegisterNullRequest() {
        assertThrows(NullPointerException.class, () -> authenticationService.register(null));
        verify(userRepository, never()).save(any(User.class));
    }
}