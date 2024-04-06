package com.example.backend.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import jakarta.transaction.Transactional;

@Service
public class UserService implements UserDetailsService {
	@Autowired
	private UserRepository repository;
	@Autowired
	private RoleService roleService;
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public User findByEmail(String email) {
		return repository.findByEmail(email);
	}

	public User findById(User user) {
		return repository.findById(user.getId()).orElse(null);
	}

	public Optional<User> findByUsername(String username) {
		return repository.findByUsername(username);
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
				String.format("Пользователь '%s' не найден", username)));
		return new org.springframework.security.core.userdetails.User(
				user.getUsername(),
				user.getPassword(),
				user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName()))
						.collect(Collectors.toList()));
	}

	public User createNewUser(User registrationUserDto) {
		User user = new User();
		user.setUsername(registrationUserDto.getUsername());
		user.setEmail(registrationUserDto.getEmail());
		user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));
		user.setRoles(List.of(roleService.getUserRole()));
		return repository.save(user);
	}
}
