package com.example.backend.controller.facades;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.example.backend.dto.AdvertisementDTO;
import com.example.backend.dto.CategoryDTO;
import com.example.backend.model.Advertisement;
import com.example.backend.model.Category;
import com.example.backend.service.AdvertisementService;
import com.example.backend.service.CategoryService;

@Component
public class MainFacade {
    @Autowired
    private AdvertisementService aService;
    @Autowired
    private CategoryService cService;

    public List<AdvertisementDTO> getAdds() {
        try {
            Thread.sleep(1000);
        } catch (Exception exception) {

        }
        List<Advertisement> adds = aService.findAll();
        return adds.stream().map(add -> aService.mapToDto(add)).collect(Collectors.toList());
    }

    public List<AdvertisementDTO> search(String name) {
        return aService.findByName(name);
    }

    public List<AdvertisementDTO> getByCat(Long id) {
        return aService.findByCategoryIdOrChildCategoryIds(id);
    }

    public ResponseEntity<?> getAddById(Long id) {
        Advertisement add = aService.findById(id);
        add.setViews(add.getViews() + 1);
        aService.save(add);
        return ResponseEntity.ok(aService.mapToDto(add));
    }

    public List<CategoryDTO> getAll() {
        List<Category> catList = cService.findAll();
        return catList.stream().map(category -> cService.mapToDTO(category)).collect(Collectors.toList());
    }

    public List<AdvertisementDTO> getSimilars(Long cat_id, double price,
            Long id) {
        return aService.findSimilars(cat_id, price, id);
    }
}
