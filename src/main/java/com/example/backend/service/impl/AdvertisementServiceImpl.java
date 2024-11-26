package com.example.backend.service.impl;

import com.example.backend.dto.request.AdvertisementFilterRequest;
import com.example.backend.dto.request.AdvertisementRequest;
import com.example.backend.dto.response.AdvertisementResponse;
import com.example.backend.exception.ForbiddenException;
import com.example.backend.filter.FilterSpecification;
import com.example.backend.mapper.AdvertisementMapper;
import com.example.backend.model.Advertisement;
import com.example.backend.model.Category;
import com.example.backend.model.User;
import com.example.backend.model.UserSubscription;
import com.example.backend.repository.AdvertisementRepository;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.service.AdvertisementService;
import com.example.backend.service.FileService;
import com.example.backend.service.NotificationService;
import com.example.backend.service.CategoryService;
import com.example.backend.service.messaging.MessageProducer;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.jpa.domain.Specification;
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
@Slf4j
public class AdvertisementServiceImpl implements AdvertisementService {
        private final AdvertisementMapper mapper = Mappers.getMapper(AdvertisementMapper.class);
        private final AdvertisementRepository repository;
        private final CategoryRepository categoryRepository;
        private final FileService fileService;
        private final NotificationService nService;
        private final CategoryService cService;
        private final MessageSource messageSource;
        private final List<FilterSpecification<Advertisement>> filterSpecifications;
        private final AdvertisementRepository advertisementRepository;
        private final MessageProducer messageProducer;

        @Autowired
        public AdvertisementServiceImpl(AdvertisementRepository repository,
                                        CategoryRepository categoryRepository,
                                        FileService fileService,
                                        NotificationService nService,
                                        CategoryService cService,
                                        MessageSource messageSource,
                                        List<FilterSpecification<Advertisement>> filterSpecifications,
                                        AdvertisementRepository advertisementRepository,
                                        MessageProducer messageProducer) {
                this.repository = repository;
                this.categoryRepository = categoryRepository;
                this.fileService = fileService;
                this.nService = nService;
                this.cService = cService;
                this.messageSource = messageSource;
                this.filterSpecifications = filterSpecifications;
                this.advertisementRepository = advertisementRepository;
                this.messageProducer = messageProducer;
        }

        @Override
        public List<AdvertisementResponse> findAll() {
                List<Advertisement> advertisements = repository.findAll();
                return advertisements.stream().map(mapper::toResponse).collect(Collectors.toList());
        }

        @Override
        public Advertisement save(Advertisement advertisement) {
                return repository.save(advertisement);
        }

        @Override
        public AdvertisementResponse createAdvertisement(AdvertisementRequest advertisementRequest,
                                                         List<MultipartFile> images) {
                User user = getCurrentUser();
                Category category = findCategory(advertisementRequest.getCategoryId());
                List<String> imagesUrl = processImages(images);

                Advertisement advertisement = createAdvertisementEntity(advertisementRequest, user, category, imagesUrl);
                String url = messageProducer.getShortUrl(advertisement.getId());

                advertisement.setShortUrl(url);
                advertisement = save(advertisement);

                return mapper.toResponse(advertisement);
        }

        @Override
        public AdvertisementResponse updateAdvertisement(AdvertisementRequest advertisementRequest,
                                                         List<MultipartFile> images, UUID advertisementId) {
                Advertisement existingAdvertisement = repository.findById(advertisementId).orElseThrow(
                        () -> new EntityNotFoundException(messageSource.getMessage(
                                "advertisement.not.found", new Object[]{advertisementId.toString()},
                                LocaleContextHolder.getLocale())));

                User user = getCurrentUser();
                if (!user.getEmail().equals(existingAdvertisement.getUser().getEmail())) {
                        throw new ForbiddenException(messageSource.getMessage(
                                "advertisement.forbidden.edit", null, LocaleContextHolder.getLocale()));
                }

                Category category = findCategory(advertisementRequest.getCategoryId());
                List<String> imagesUrl = processImages(images);

                Advertisement newAdvertisement = updateAdvertisementEntity(
                        advertisementRequest,
                        existingAdvertisement,
                        category,
                        imagesUrl,
                        user);
                newAdvertisement = save(newAdvertisement);

                nService.notifySubscribers(messageSource.getMessage(
                        "advertisement.updated", new Object[]{existingAdvertisement.getTitle()},
                        LocaleContextHolder.getLocale()), existingAdvertisement.getSubscriptions());

                return mapper.toResponse(newAdvertisement);
        }

        @Override
        public AdvertisementResponse findById(UUID id) {
                Advertisement advertisement = repository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException(messageSource.getMessage(
                                "advertisement.not.found", new Object[]{id.toString()},
                                LocaleContextHolder.getLocale())));
                return mapper.toResponse(advertisement);
        }

        @Override
        public void deleteById(UUID id) {
                Advertisement advertisement = repository.findById(id).orElseThrow(
                        () -> new EntityNotFoundException(messageSource.getMessage(
                                "advertisement.not.found", new Object[]{id.toString()},
                                LocaleContextHolder.getLocale())));

                List<UserSubscription> userSubscriptions = advertisement.getSubscriptions();
                nService.notifySubscribers(messageSource.getMessage(
                        "advertisement.deleted", new Object[]{advertisement.getTitle()},
                        LocaleContextHolder.getLocale()), userSubscriptions);

                repository.deleteById(id);
        }

        @Override
        public List<AdvertisementResponse> findByName(String name) {
                return repository.findByTitle(name).stream().map(mapper::toResponse)
                        .collect(Collectors.toList());
        }

        @Override
        public List<AdvertisementResponse> findByCategory(UUID id) {
                return repository.findByCategoryId(id).stream().map(mapper::toResponse)
                        .collect(Collectors.toList());
        }

        @Override
        public List<AdvertisementResponse> findByFiler(AdvertisementFilterRequest filterRequest) {
                Specification<Advertisement> finalSpec = filterSpecifications.stream()
                        .filter(spec -> spec.isApplicable(filterRequest))
                        .map(spec -> spec.toSpecification(filterRequest))
                        .reduce(Specification.where(null), Specification::and);

                List<Advertisement> advertisements = advertisementRepository.findAll(finalSpec);

                return advertisements.stream().map(mapper::toResponse).toList();
        }

        @Override
        public List<AdvertisementResponse> findByCategoryIdOrChildCategoryIds(UUID categoryId) {
                List<UUID> categoryIds = cService.findAllChildCategoryIds(categoryId);
                categoryIds.add(categoryId);

                List<Advertisement> advertisements = repository.findByCategoryIdIn(categoryIds);

                return advertisements.stream().map(mapper::toResponse).collect(Collectors.toList());
        }

        @Override
        public List<AdvertisementResponse> findSimilars(UUID catId, double price, UUID id) {
                Category category = findCategory(catId);
                List<Advertisement> advertisements = repository.findSimilars(category, price, id);

                return advertisements.stream().map(mapper::toResponse)
                        .collect(Collectors.toList());
        }

        // Utility methods for separation of concerns and reusability

        private User getCurrentUser() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                return (User) authentication.getPrincipal();
        }

        private Category findCategory(UUID categoryId) {
                return categoryRepository.findById(categoryId).orElseThrow(
                        () -> new EntityNotFoundException(messageSource.getMessage(
                                "category.not.found", new Object[]{categoryId.toString()},
                                LocaleContextHolder.getLocale())));
        }

        private Advertisement createAdvertisementEntity(AdvertisementRequest request, User user,
                                                        Category category, List<String> imagesUrl) {
                Advertisement advertisement = mapper.toEntity(request);
                advertisement.setId(UUID.randomUUID());
                advertisement.setUser(user);
                advertisement.setCategory(category);
                advertisement.setImagesUrl(imagesUrl);
                return advertisement;
        }

        private Advertisement updateAdvertisementEntity(AdvertisementRequest request, Advertisement existingAdvertisement,
                                                        Category category, List<String> imagesUrl, User user) {
                Advertisement newAdvertisement = mapper.toEntity(request);
                newAdvertisement.setId(existingAdvertisement.getId());
                newAdvertisement.setUser(user);
                newAdvertisement.setCategory(category);
                newAdvertisement.setImagesUrl(imagesUrl);
                newAdvertisement.setSubscriptions(existingAdvertisement.getSubscriptions());
                return newAdvertisement;
        }

        public List<String> processImages(List<MultipartFile> images) {
                return (images != null ? images.stream()
                        .map(i -> {
                                try {
                                        return fileService.saveFile(i);
                                } catch (IOException e) {
                                        log.error("Error saving image: " + e.getMessage(), e);
                                        return null;
                                } catch (Exception e) {
                                        throw new RuntimeException(e);
                                }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList()) : Collections.emptyList());
        }
}