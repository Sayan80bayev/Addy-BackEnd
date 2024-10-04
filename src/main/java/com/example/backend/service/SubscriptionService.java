package com.example.backend.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.backend.dto.response.UserSubscriptionResponse;
import com.example.backend.mapper.UserSubscriptionMapper;
import com.example.backend.model.Advertisement;
import com.example.backend.model.UserSubscription;
import com.example.backend.repository.SubscribtionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscribtionRepository repository;
    private final UserSubscriptionMapper mapper = Mappers.getMapper(UserSubscriptionMapper.class);

    public void save(UserSubscription u) {
        repository.save(u);
    }

    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    public void deleteSubs(UUID user_id, UUID id) {
        repository.deleteSubs(user_id, id);
    }

    public List<UserSubscriptionResponse> getSubcribtionsByUserID(UUID id) {
        return repository.getSubcribtionsByUserID(id).stream().map(mapper::toResponse).collect(Collectors.toList());
    }
}
