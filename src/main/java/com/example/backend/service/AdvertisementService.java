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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.mapstruct.factory.Mappers;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
@Slf4j
public class AdvertisementService {
        private final AdvertisementMapper mapper = Mappers.getMapper(AdvertisementMapper.class);

        private final AdvertisementRepository repository;
        private final CategoryRepository categoryRepository;

        private final FileService fileService;
        private final NotificationService nService;
        private final CategoryService cService;

        private final MessageSource messageSource;

        public List<AdvertisementResponse> findAll() {
                List<Advertisement> advertisements = repository.findAll();
                return advertisements.stream().map(mapper::toResponse).collect(Collectors.toList());
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
                                () -> new EntityNotFoundException(messageSource.getMessage(
                                                "category.not.found", new Object[] { categoryId.toString() },
                                                LocaleContextHolder.getLocale())));

                Advertisement advertisement = mapper.toEntity(advertisementRequest);

                List<String> imagesUrl = processImages(images);

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
                                () -> new EntityNotFoundException(messageSource.getMessage(
                                                "advertisement.not.found", new Object[] { advertisementId.toString() },
                                                LocaleContextHolder.getLocale())));

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                User userDetails = (User) authentication.getPrincipal();
                String email = userDetails.getEmail();

                if (!email.equals(existingAdvertisement.getUser().getEmail())) {
                        throw new ForbiddenException(messageSource.getMessage(
                                        "advertisement.forbidden.edit", null, LocaleContextHolder.getLocale()));
                }

                UUID categoryId = advertisementRequest.getCategoryId();
                Category category = categoryRepository.findById(categoryId).orElseThrow(
                                () -> new EntityNotFoundException(messageSource.getMessage(
                                                "category.not.found", new Object[] { categoryId.toString() },
                                                LocaleContextHolder.getLocale())));

                Advertisement newAdvertisement = mapper.toEntity(advertisementRequest);
                List<UserSubscription> subscribers = existingAdvertisement.getSubscriptions();

                List<String> imagesUrl = processImages(images);

                newAdvertisement.setId(advertisementId);
                newAdvertisement.setUser(userDetails);
                newAdvertisement.setCategory(category);
                newAdvertisement.setImagesUrl(imagesUrl);
                newAdvertisement.setSubscriptions(subscribers);
                newAdvertisement = this.save(newAdvertisement);

                nService.notifySubscribers(messageSource.getMessage(
                                "advertisement.updated", new Object[] { existingAdvertisement.getTitle() },
                                LocaleContextHolder.getLocale()), subscribers);

                return mapper.toResponse(newAdvertisement);
        }

        public AdvertisementResponse findById(UUID id) {
                Advertisement advertisement = repository.findById(id).orElseThrow(
                                () -> new EntityNotFoundException(messageSource.getMessage(
                                                "advertisement.not.found", new Object[] { id.toString() },
                                                LocaleContextHolder.getLocale())));
                return mapper.toResponse(advertisement);
        }

        public void deleteById(UUID id) {
                Advertisement add = repository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException(messageSource.getMessage(
                                                "advertisement.not.found", new Object[] { id.toString() },
                                                LocaleContextHolder.getLocale())));

                List<UserSubscription> userSubscriptions = add.getSubscriptions();

                nService.notifySubscribers(messageSource.getMessage(
                                "advertisement.deleted", new Object[] { add.getTitle() },
                                LocaleContextHolder.getLocale()), userSubscriptions);

                repository.deleteById(id);
        }

        public List<AdvertisementResponse> findByName(String name) {
                return repository.findByTitle(name).stream().map(a -> mapper.toResponse(a))
                                .collect(Collectors.toList());
        }

        public List<AdvertisementResponse> findByCategory(UUID id) {
                return repository.findByCategoryId(id).stream().map(a -> mapper.toResponse(a))
                                .collect(Collectors.toList());
        }

        public List<AdvertisementResponse> findByCategoryIdOrChildCategoryIds(UUID categoryId) {
                List<UUID> categoryIds = cService.findAllChildCategoryIds(categoryId);
                categoryIds.add(categoryId);

                List<Advertisement> advertisements = repository.findByCategoryIdIn(categoryIds);

                return advertisements.stream().map(mapper::toResponse).collect(Collectors.toList());
        }

        public List<AdvertisementResponse> findSimilars(UUID catId, double price, UUID id) {
                Category cat = categoryRepository.findById(catId).orElseThrow(
                                () -> new EntityNotFoundException(messageSource.getMessage(
                                                "category.not.found", new Object[] { catId.toString() },
                                                LocaleContextHolder.getLocale())));

                List<Advertisement> advertisement = repository.findSimilars(cat, price, id);
                List<AdvertisementResponse> aDtos = advertisement.stream().map(mapper::toResponse)
                                .collect(Collectors.toList());

                return aDtos;
        }

        private List<String> processImages(List<MultipartFile> images) {

                List<String> imagesUrl = (images != null ? images.stream()
                                .map(i -> {
                                        try {
                                                return fileService.saveFile(i);
                                        } catch (IOException e) {
                                                log.error(e.getMessage());
                                                e.printStackTrace();
                                                return null;
                                        }
                                })
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList()) : Collections.emptyList());

                return imagesUrl;
        }
}
