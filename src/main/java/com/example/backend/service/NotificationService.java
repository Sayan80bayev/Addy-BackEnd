package com.example.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.backend.config.WebSocketHandler;
import com.example.backend.model.Notification;
import com.example.backend.model.UserSubscription;
import com.example.backend.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository repository;
    private final WebSocketHandler webSocketHandler;

    public void notifySubscribers(String message, List<UserSubscription> uSubs) {
        for (UserSubscription uSub : uSubs) {
            try {
                String recipient = uSub.getUser().getId().toString();
                Notification notification = Notification.builder()
                        .id(UUID.randomUUID())
                        .date(LocalDateTime.now())
                        .value(message).build();

                webSocketHandler.sendMessageToUser(recipient, notification);

                repository.save(notification);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                e.printStackTrace();
            }
        }
        log.info("Successfully notified all users");
    }
}
