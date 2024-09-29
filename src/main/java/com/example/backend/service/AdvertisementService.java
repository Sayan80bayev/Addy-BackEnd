package com.example.backend.service;

import com.example.backend.dto.request.AdvertisementRequest;
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

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvertisementService {
    private final UserService userService;
    private final AdvertisementRepository repository;
    private final NotificationService nService;
    private final CategoryService cService;
    private final AdvertisementMapper mapper;

    private SortingStrategy sortingStrategy;

    public List<AdvertisementResponse> findAll() {
        List<AdvertisementResponse> ads = repository.findAll().stream().map(mapper::toResponse)
                .collect(Collectors.toList());
        if (sortingStrategy == null)
            return ads;
        return sortingStrategy.sort(ads);
    }

    private Advertisement save(Advertisement advertisement) {
        return repository.save(advertisement);
    }

    public AdvertisementResponse createAdvertisement(AdvertisementRequest advertisementRequest,
            List<MultipartFile> images) {
        Advertisement advertisement = mapper.toEntity(advertisementRequest);
        Category category = cService.findById(advertisementRequest.getCategoryId());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User userDetails = (User) authentication.getPrincipal(); // Assume you have UserDetailsImpl
        String email = userDetails.getEmail(); // Method to retrieve the email from UserDetails

        User user = userService.findByEmail(email); // Assuming userService can find users by email

        advertisement.setUser(user);
        advertisement.setCategory(category);

        advertisement = this.save(advertisement);
        return mapper.toResponse(advertisement);
    }

    public AdvertisementResponse updateAdvertisement(AdvertisementRequest advertisementRequest,
            List<MultipartFile> images, UUID advertisementId) {
        Advertisement existingAdvertisement = repository.findById(advertisementId).orElseThrow(() -> null);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userDetails = (User) authentication.getPrincipal(); // Assume you have UserDetailsImpl
        String email = userDetails.getEmail(); // Method to retrieve the email from UserDetails

        boolean isEmailEqual = email.equals(existingAdvertisement.getUser().getEmail());

        Advertisement newAdvertisement = mapper.toEntity(advertisementRequest);
        Category category = cService.findById(advertisementRequest.getCategoryId());

        newAdvertisement.setCategory(category);
        newAdvertisement = this.save(newAdvertisement);

        return mapper.toResponse(newAdvertisement);
    }

    public AdvertisementResponse findById(UUID id) {
        Advertisement advertisement = repository.findById(id).orElseThrow();
        return mapper.toResponse(advertisement);
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
