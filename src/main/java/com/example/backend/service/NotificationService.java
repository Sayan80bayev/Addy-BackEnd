package com.example.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.dto.NotificationDTO;
import com.example.backend.model.Notification;
import com.example.backend.repository.NotificationRepository;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository repository;

    public void save(Notification n) {
        repository.save(n);
    }

    public Notification findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<NotificationDTO> findByEmail(String email) {
        return repository.findByEmail(email) // Assuming you have this repository method
                .stream()
                .map(n -> toDTO(n)) // Use method reference for cleaner code
                .collect(Collectors.toList());
    }

    public void seen(List<Notification> notifications) {
        for (int i = 0; i < notifications.size(); i++) {
            notifications.get(i).setSeen(true);
            repository.save(notifications.get(i));
        }
    }

    public static NotificationDTO toDTO(Notification notification) {
        return NotificationDTO.builder()
                .value(notification.getValue())
                .id(notification.getId())
                .date(notification.getDate())
                .build();
    }
}
