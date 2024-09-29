package com.example.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;

    public User findById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }

    public boolean saveUser(User user) {
        try {
            repository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void deleteUser(User user) {
        repository.delete(user);
    }

}
