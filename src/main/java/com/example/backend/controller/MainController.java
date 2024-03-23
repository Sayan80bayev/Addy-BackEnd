package com.example.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.backend.model.Advertisement;
import com.example.backend.service.AdvertisementService;

@Controller
public class MainController {
    @Autowired
    private AdvertisementService service;
    @GetMapping("/")
    public String viewHomePage(Model model) {
        List<Advertisement> listAdvertisements = service.findAll();
        model.addAttribute("listAdvertisements", listAdvertisements);
        return "index";
    }
}
