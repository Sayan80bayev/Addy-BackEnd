package com.example.backend.repository;

import org.springframework.data.repository.CrudRepository;
import com.example.backend.model.Advertisment;

public interface AdvertismentRepo extends CrudRepository <Advertisment, Long>{
    
}
