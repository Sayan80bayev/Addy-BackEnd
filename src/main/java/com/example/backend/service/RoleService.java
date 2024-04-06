package com.example.backend.service;

import org.springframework.stereotype.Service;

import com.example.backend.model.Role;
import com.example.backend.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getUserRole() {
        return roleRepository.findByName("ROLE_USER").get();
    }
}
