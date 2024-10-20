package com.example.backend.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.backend.dto.request.SubscribeRequest;
import com.example.backend.dto.response.UserSubscriptionResponse;
import com.example.backend.mapper.UserSubscriptionMapper;
import com.example.backend.model.Advertisement;
import com.example.backend.model.User;
import com.example.backend.model.UserSubscription;
import com.example.backend.repository.AdvertisementRepository;
import com.example.backend.repository.SubscribtionRepository;
import com.example.backend.service.SubscriptionService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final MessageSource messageSource;
    private final AdvertisementRepository advertisementRepository;
    private final SubscribtionRepository repository;
    private final UserSubscriptionMapper mapper = Mappers.getMapper(UserSubscriptionMapper.class);

    @Override
    public void subscribeUser(SubscribeRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Advertisement advertisement = advertisementRepository.findById(request.getId())
                .orElseThrow(
                        () -> new EntityNotFoundException(messageSource.getMessage(
                                "advertisement.not.found", new Object[] { request.getId().toString() },
                                LocaleContextHolder.getLocale())));

        User user = (User) authentication.getPrincipal();
        UserSubscription u = UserSubscription.builder()
                .ad(advertisement)
                .user(user)
                .id(UUID.randomUUID())
                .build();

        repository.save(u);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteSubs(SubscribeRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        repository.deleteSubs(user.getId(), request.getId());
    }

    @Override
    public List<UserSubscriptionResponse> getSubcribtionsByUserID(UUID id) {
        return repository.getSubcribtionsByUserID(id).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }
}