package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.User;

public interface UserRepository extends JpaRepository <User, Long> {
    public User findByEmailAndPassword(String email, String password);
	
	public User findByEmail(String email);
}
