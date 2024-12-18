package com.example.backend.service.impl;

import com.example.backend.config.JwtService;
import com.example.backend.dto.request.JwtRequest;
import com.example.backend.dto.request.RegisterRequest;
import com.example.backend.dto.response.JwtResponse;
import com.example.backend.enums.Role;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.service.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;

        @Override
        public JwtResponse register(@Valid RegisterRequest request) {
                User user = User.builder()
                        .id(UUID.randomUUID())
                        .name(request.getUsername())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(Role.USER)
                        .build();
                userRepository.save(user);
                String jwtToken = jwtService.generateToken(user);
                return JwtResponse.builder()
                        .token(jwtToken)
                        .build();
        }

        @Override
        public JwtResponse authenticate(@Valid JwtRequest request) {
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()));

                User user = userRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new RuntimeException("User not found"));

                String jwtToken = jwtService.generateToken(user);

                JwtResponse response = new JwtResponse();
                response.setToken(jwtToken);
                return response;
        }
}