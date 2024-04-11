package com.example.backend.service;

import com.example.backend.dto.AdvertisementDTO;
import com.example.backend.model.Advertisement;
import com.example.backend.repository.AdvertisementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdvertisementService {
    @Autowired
    private AdvertisementRepository repository;

    public List<Advertisement> findAll() {
        return repository.findAll();
    }

    public Advertisement save(Advertisement advertisement) {
        return repository.save(advertisement);
    }

    public Advertisement findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Advertisement update(Advertisement advertisement) {
        Advertisement add = repository.findById(advertisement.getId()).orElse(null);
        add.setTitle(advertisement.getTitle());
        add.setDescription(advertisement.getDescription());
        add.setCategory(advertisement.getCategory());
        add.setPrice(advertisement.getPrice());
        return repository.save(add);
    }

    public AdvertisementDTO mapToDto(Advertisement advertisement) {
        AdvertisementDTO advertisementDTO = AdvertisementDTO
                .builder()
                .id(advertisement.getId())
                .description(advertisement.getDescription())
                .title(advertisement.getTitle())
                .price(advertisement.getPrice())
                .views(advertisement.getViews())
                .category_id(advertisement.getCategory().getId())
                .user_id(advertisement.getUser().getId())
                .build();
        return advertisementDTO;
    }
}
