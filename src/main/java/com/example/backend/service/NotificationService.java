package com.example.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.backend.model.User;
import com.example.backend.model.UserSubscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifySubscribers(String message, List<UserSubscription> uSubs) {
        for (UserSubscription uSub : uSubs) {
            String recipient = "/notifications/" + uSub.getUser().getId();
            messagingTemplate.convertAndSend(recipient, message);
        }
        log.info("Successfully notified all users");
    }
}
