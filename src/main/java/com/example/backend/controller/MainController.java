package com.example.backend.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.AdvertisementDTO;
import com.example.backend.dto.CategoryDTO;
import com.example.backend.model.Advertisement;
import com.example.backend.model.Category;
import com.example.backend.service.AdvertisementService;
import com.example.backend.service.CategoryService;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1/public")
public class MainController {
    @Autowired
    private AdvertisementService aService;
    @Autowired
    private CategoryService cService;

    @GetMapping("/getAdds")
    public List<AdvertisementDTO> getAdds() {
        List<Advertisement> adds = aService.findAll();
        return adds.stream().map(add -> aService.mapToDto(add)).collect(Collectors.toList());
    }

    @GetMapping("/search/{name}")
    public List<AdvertisementDTO> search(@PathVariable("name") String name) {
        return aService.findByName(name);
    }

    @GetMapping("/cat/{id}")
    public List<AdvertisementDTO> getByCat(@PathVariable("id") Long id) {
        return aService.findByCategory(id);
    }

    @GetMapping("/add/{id}")
    public AdvertisementDTO getAddById(@PathVariable("id") Long id) {
        Advertisement add = aService.findById(id);

        return aService.mapToDto(add);
    }

    @GetMapping("/getCats")
    public List<CategoryDTO> getAll() {
        List<Category> catList = cService.findAll();
        return catList.stream().map(category -> cService.mapToDTO(category)).collect(Collectors.toList());
    }
}