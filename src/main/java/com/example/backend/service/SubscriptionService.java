package com.example.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.dto.UserSubscriptionDTO;
import com.example.backend.model.Advertisement;
import com.example.backend.model.UserSubscription;
import com.example.backend.repository.SubscribtionRepository;

@Service
public class SubscriptionService {
    @Autowired
    private SubscribtionRepository repository;

    public void save(UserSubscription u) {
        repository.save(u);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public void deleteSubs(Long user_id, Long id) {
        repository.deleteSubs(user_id, id);
    }

    public List<UserSubscriptionDTO> getSubcribtionsByUserID(Long id) {
        return repository.getSubcribtionsByUserID(id).stream().map(s -> toDTO(s)).collect(Collectors.toList());
    }

    public UserSubscriptionDTO toDTO(UserSubscription userSubscription) {
        return UserSubscriptionDTO.builder()
                .id(userSubscription.getId())
                .user_id(userSubscription.getUser().getId())
                .advertisement_id(userSubscription.getAd().getId())
                .build();
    }
}
