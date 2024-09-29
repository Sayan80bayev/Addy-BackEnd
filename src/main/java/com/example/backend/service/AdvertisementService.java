package com.example.backend.service;

import com.example.backend.dto.response.AdvertisementResponse;
import com.example.backend.mapper.AdvertisementMapper;
import com.example.backend.model.Advertisement;
import com.example.backend.model.Category;
import com.example.backend.model.Notification;
import com.example.backend.model.User;
import com.example.backend.model.UserSubscription;
import com.example.backend.repository.AdvertisementRepository;
import com.example.backend.service.sortStrategy.SortingStrategy;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvertisementService {
    private final AdvertisementRepository repository;
    private final NotificationService nService;
    private final CategoryService cService;
    private final AdvertisementMapper mapper;

    private SortingStrategy sortingStrategy;

    public List<Advertisement> findAll() {
        List<Advertisement> ads = repository.findAll();
        if (sortingStrategy == null)
            return ads;
        return sortingStrategy.sort(ads);
    }

    public Advertisement save(Advertisement advertisement) {
        return repository.save(advertisement);
    }

    public Advertisement findById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public List<AdvertisementResponse> findByName(String name) {
        return repository.findByTitle(name).stream().map(a -> mapper.toResponse(a)).collect(Collectors.toList());
    }

    public List<AdvertisementResponse> findByCategory(UUID id) {
        return repository.findByCategoryId(id).stream().map(a -> mapper.toResponse(a)).collect(Collectors.toList());
    }

    public void deleteById(UUID id) {
        Advertisement add = repository.findById(id).orElse(null);
        // notifyUsers(add.getTitle());
        repository.deleteById(id);
    }

    public List<AdvertisementResponse> findByCategoryIdOrChildCategoryIds(UUID categoryId) {
        List<UUID> categoryIds = cService.findAllChildCategoryIds(categoryId);
        categoryIds.add(categoryId);
        List<Advertisement> advertisements = repository.findByCategoryIdIn(categoryIds);
        return advertisements.stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    public List<AdvertisementResponse> findSimilars(UUID cat_id, double price, UUID id) {
        Category cat = cService.findById(cat_id);
        List<Advertisement> advertisement = repository.findSimilars(cat, price, id);
        List<AdvertisementResponse> aDtos = advertisement.stream().map(mapper::toResponse).collect(Collectors.toList());
        return aDtos;
    }

    public void notifyUsers(List<UserSubscription> l, String value) {
        for (int i = 0; i < l.size(); i++) {
            User cUser = l.get(i).getUser();
            nService.save(
                    Notification.builder().user(cUser).value(value).seen(false).date(LocalDateTime.now()).build());
        }
    }

    public void setSortingStrategy(SortingStrategy sortingStrategy) {
        this.sortingStrategy = sortingStrategy;
    }
}
