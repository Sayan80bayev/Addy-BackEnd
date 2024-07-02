package com.example.backend.service;

import com.example.backend.dto.AdvertisementDTO;
import com.example.backend.dto.ImageDTO;
import com.example.backend.model.Advertisement;
import com.example.backend.model.Category;
import com.example.backend.model.Image;
import com.example.backend.model.Notification;
import com.example.backend.model.User;
import com.example.backend.model.UserSubscription;
import com.example.backend.repository.AdvertisementRepository;
import com.example.backend.service.sortStrategy.SortingStrategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdvertisementService {
    @Autowired
    private NotificationService nService;
    @Autowired
    private AdvertisementRepository repository;
    @Autowired
    private CategoryService cService;
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

    public Advertisement findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<AdvertisementDTO> findByName(String name) {
        return repository.findByTitle(name).stream().map(a -> mapToDto(a)).collect(Collectors.toList());
    }

    public List<AdvertisementDTO> findByCategory(Long id) {
        return repository.findByCategoryId(id).stream().map(aDto -> mapToDto(aDto)).collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        Advertisement add = repository.findById(id).orElse(null);
        // notifyUsers(add.getTitle());
        repository.deleteById(id);
    }

    public void update(Advertisement advertisement) {
        notifyUsers(advertisement.getSubscriptions(), advertisement.getTitle() + " has been updated");
    }

    public List<AdvertisementDTO> findByCategoryIdOrChildCategoryIds(Long categoryId) {
        List<Long> categoryIds = cService.findAllChildCategoryIds(categoryId);
        categoryIds.add(categoryId);
        List<Advertisement> advertisements = repository.findByCategoryIdIn(categoryIds);
        return advertisements.stream()
                .map(a -> mapToDto(a))
                .collect(Collectors.toList());
    }

    public AdvertisementDTO mapToDto(Advertisement advertisement) {
        AdvertisementDTO advertisementDTO = AdvertisementDTO
                .builder()
                .id(advertisement.getId())
                .description(advertisement.getDescription())
                .title(advertisement.getTitle())
                .price(advertisement.getPrice())
                .views(advertisement.getViews())
                .category(cService.mapToDTO(advertisement.getCategory()))
                .email(advertisement.getUser().getEmail())
                .date(advertisement.getDate())
                .build();
        List<ImageDTO> imageDTOs = advertisement.getImages().stream().map(image -> toDTO(image))
                .collect(Collectors.toList());
        advertisementDTO.setImages(imageDTOs);
        return advertisementDTO;
    }

    public static ImageDTO toDTO(Image image) {
        ImageDTO dto = new ImageDTO();
        dto.setId(image.getId());
        dto.setImageData(image.getImageData());
        return dto;
    }

    public static Image toEntity(ImageDTO dto) {
        Image entity = new Image();
        entity.setId(dto.getId());
        entity.setImageData(dto.getImageData());
        return entity;
    }

    public List<AdvertisementDTO> findSimilars(Long cat_id, double price, Long id) {
        Category cat = cService.findById(cat_id);
        List<Advertisement> advertisement = repository.findSimilars(cat, price, id);
        List<AdvertisementDTO> aDtos = advertisement.stream().map(add -> mapToDto(add)).collect(Collectors.toList());
        aDtos.stream()
                .forEach(add -> add.setImages(new ArrayList<>(Collections.singletonList(add.getImages().get(0)))));

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
