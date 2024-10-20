package com.example.backend.service;

import com.example.backend.dto.request.SubscribeRequest;
import com.example.backend.dto.response.UserSubscriptionResponse;

import java.util.List;
import java.util.UUID;

public interface SubscriptionService {
    void subscribeUser(SubscribeRequest request);
    void deleteById(UUID id);
    void deleteSubs(SubscribeRequest request);
    List<UserSubscriptionResponse> getSubcribtionsByUserID(UUID id);
}