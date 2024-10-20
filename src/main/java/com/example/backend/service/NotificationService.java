package com.example.backend.service;

import com.example.backend.model.UserSubscription;

import java.util.List;

public interface NotificationService {
    void notifySubscribers(String message, List<UserSubscription> uSubs);
}