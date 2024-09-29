package com.example.backend.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.dto.response.NotificationResponse;
import com.example.backend.mapper.NotificationMapper;
import com.example.backend.model.Notification;
import com.example.backend.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository repository;
    private final NotificationMapper mapper;

    public void save(Notification n) {
        repository.save(n);
    }

    public Notification findById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public List<NotificationResponse> findByEmail(String email) {
        return repository.findByEmail(email) // Assuming you have this repository method
                .stream()
                .map(mapper::toResponse) // Use method reference for cleaner code
                .collect(Collectors.toList());
    }

    public void seen(List<Notification> notifications) {
        for (int i = 0; i < notifications.size(); i++) {
            notifications.get(i).setSeen(true);
            repository.save(notifications.get(i));
        }
    }

}
