package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;

@Service
public class UserService {
    @Autowired UserRepository repository;

    public boolean saveUser(User s) {
		User save = repository.save(s);
		if (save.getId() != null) {
			return true;
		}
		return false;
	}

	public User checkLogin(User s) {
		return repository.findByEmailAndPassword(s.getEmail(), s.getPassword());
	}
		
	public boolean getUserByEmail(String email) {
		 User byStudentEmail = repository.findByEmail(email);
		 return byStudentEmail != null ? true : false;
	}

}
