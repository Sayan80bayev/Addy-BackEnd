package com.example.backend.service;

import com.example.backend.dto.AdvertisementDTO;
import com.example.backend.dto.ImageDTO;
import com.example.backend.model.Advertisement;
import com.example.backend.model.Category;
import com.example.backend.model.Image;
import com.example.backend.repository.AdvertisementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdvertisementService {
    @Autowired
    private AdvertisementRepository repository;
    @Autowired
    private CategoryService cService;

    public List<Advertisement> findAll() {
        return repository.findAll();
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
        repository.deleteById(id);
    }

    public Advertisement update(Advertisement advertisement) {
        Advertisement add = repository.findById(advertisement.getId()).orElse(null);
        add.setTitle(advertisement.getTitle());
        add.setDescription(advertisement.getDescription());
        add.setCategory(advertisement.getCategory());
        add.setPrice(advertisement.getPrice());
        add.setDate(advertisement.getDate());
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
        return advertisement.stream().map(add -> mapToDto(add)).collect(Collectors.toList());
    }
}
