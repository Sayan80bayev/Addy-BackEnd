package com.example.backend.service;

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
}
