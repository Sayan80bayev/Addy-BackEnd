package com.example.backend.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.response.AdvertisementResponse;
import com.example.backend.dto.response.CategoryResponse;

@RestController
@RequestMapping("/api/v1/public")
public class MainController {

    @GetMapping("/getCats")
    public List<CategoryResponse> getAll() {
        return facade.getAll();
    }

    @GetMapping("/getSimilars")
    public List<AdvertisementResponse> getSimilars(@RequestParam("cat") Long cat_id,
            @RequestParam("price") double price,
            @RequestParam("id") Long id) {
        return facade.getSimilars(cat_id, price, id);
    }

    @GetMapping("/sort/{id}")
    public ResponseEntity<?> sortAdds(@PathVariable Long id) {
        return facade.sortAdds(id);
    }
}