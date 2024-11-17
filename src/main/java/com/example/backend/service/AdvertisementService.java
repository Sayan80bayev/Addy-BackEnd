package com.example.backend.service;

import com.example.backend.dto.request.AdvertisementFilterRequest;
import com.example.backend.dto.request.AdvertisementRequest;
import com.example.backend.dto.response.AdvertisementResponse;
import com.example.backend.model.Advertisement;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface AdvertisementService {
    List<AdvertisementResponse> findAll();
    List<AdvertisementResponse> findByCategoryIdOrChildCategoryIds(UUID categoryId);
    List<AdvertisementResponse> findSimilars(UUID catId, double price, UUID id);
    List<AdvertisementResponse> findByName(String name);
    List<AdvertisementResponse> findByCategory(UUID id) ;
    List<AdvertisementResponse> findByFiler(AdvertisementFilterRequest advertisementRequest);
    Advertisement save(Advertisement advertisement);

    AdvertisementResponse findById(UUID id);
    AdvertisementResponse updateAdvertisement(AdvertisementRequest advertisementRequest, List<MultipartFile> files, UUID advertisementId);
    AdvertisementResponse createAdvertisement(AdvertisementRequest advertisementRequest, List<MultipartFile> images);

    void deleteById(UUID id);
}
