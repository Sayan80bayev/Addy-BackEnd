package com.example.backend.service;

import com.example.backend.dto.request.AdvertisementRequest;
import com.example.backend.dto.response.AdvertisementResponse;
import com.example.backend.exception.ForbiddenException;
import com.example.backend.mapper.AdvertisementMapper;
import com.example.backend.model.Advertisement;
import com.example.backend.model.Category;
import com.example.backend.model.User;
import com.example.backend.model.UserSubscription;
import com.example.backend.repository.AdvertisementRepository;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.mapstruct.factory.Mappers;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvertisementService {
    private final AdvertisementMapper mapper = Mappers.getMapper(AdvertisementMapper.class);

    private final AdvertisementRepository repository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private final FileService fileService;
    private final CategoryService cService;
    private final NotificationService nService;

    public List<AdvertisementResponse> findAll() {
        List<Advertisement> advertisements = repository.findAll();
        List<AdvertisementResponse> ads = advertisements.stream().map(mapper::toResponse)
                .collect(Collectors.toList());
        return ads;
    }

    private Advertisement save(Advertisement advertisement) {
        return repository.save(advertisement);
    }

    public AdvertisementResponse createAdvertisement(AdvertisementRequest advertisementRequest,
            List<MultipartFile> images) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();

        UUID categoryId = advertisementRequest.getCategoryId();

        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new EntityNotFoundException("Category with id: " + categoryId.toString() + " not found"));

        Advertisement advertisement = mapper.toEntity(advertisementRequest);

        List<String> imagesUrl = (images != null ? images.stream()
                .map(i -> {
                    try {
                        return fileService.saveFile(i);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull) // Filter out null values
                .collect(Collectors.toList()) : Collections.emptyList()); // Return an empty list if images is null

        advertisement.setId(UUID.randomUUID());
        advertisement.setUser(user);
        advertisement.setCategory(category);
        advertisement.setImagesUrl(imagesUrl);

        advertisement = this.save(advertisement);
        return mapper.toResponse(advertisement);
    }

    public AdvertisementResponse updateAdvertisement(AdvertisementRequest advertisementRequest,
            List<MultipartFile> images, UUID advertisementId) {

        Advertisement existingAdvertisement = repository.findById(advertisementId).orElseThrow(
                () -> new EntityNotFoundException("Advertisement with id: " + advertisementId + " not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User userDetails = (User) authentication.getPrincipal(); // Assume you have UserDetailsImpl
        String email = userDetails.getEmail(); // Method to retrieve the email from UserDetails

        boolean isEmailEqual = email.equals(existingAdvertisement.getUser().getEmail());

        if (!isEmailEqual)
            throw new ForbiddenException("You're not allowed to edit this ad!");

        UUID categoryId = advertisementRequest.getCategoryId();
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                () -> new EntityNotFoundException("Category with id: " + categoryId.toString() + " not found"));

        Advertisement newAdvertisement = mapper.toEntity(advertisementRequest);

        List<UserSubscription> subscribers = existingAdvertisement.getSubscriptions();
        List<String> imagesUrl = images.stream().map(i -> {
            try {
                return fileService.saveFile(i);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }).collect(Collectors.toList());

        newAdvertisement.setCategory(category);
        newAdvertisement.setImagesUrl(imagesUrl);
        newAdvertisement.setSubscriptions(subscribers);
        newAdvertisement = this.save(newAdvertisement);

        nService.notifySubscribers(email, subscribers);

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

    public List<AdvertisementResponse> findSimilars(UUID catId, double price, UUID id) {
        Category cat = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Category with id: " + catId + " not found"));

        List<Advertisement> advertisement = repository.findSimilars(cat, price, id);
        List<AdvertisementResponse> aDtos = advertisement.stream().map(mapper::toResponse).collect(Collectors.toList());

        return aDtos;
    }

    // public void notifyUsers(List<UserSubscription> l, String value) {
    // for (int i = 0; i < l.size(); i++) {
    // User cUser = l.get(i).getUser();
    // nService.save(
    // Notification.builder().user(cUser).value(value).seen(false).date(LocalDateTime.now()).build());
    // }
    // }
}
