package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
