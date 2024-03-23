package com.example.backend.controller;

import com.example.backend.model.Advertisement;
import com.example.backend.service.AdvertisementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdvertisementController {
    @Autowired
    private AdvertisementService service;

    @GetMapping("/new")
    public String showNewAdvertisementForm(Model model) {
        Advertisement advertisement = new Advertisement();
        model.addAttribute("advertisement", advertisement);
        return "new_advertisement";
    }

    @PostMapping("/save")
    public String saveAdvertisement(@ModelAttribute("advertisement") Advertisement advertisement) {
        service.save(advertisement);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String showEditAdvertisementForm(@PathVariable("id") Long id, Model model) {
        Advertisement advertisement = service.findById(id);
        model.addAttribute("advertisement", advertisement);
        return "edit_advertisement";
    }
    @GetMapping("/view/{id}")
    public String viewAdvertisement(@PathVariable("id") Long id, Model model) {
        Advertisement advertisement = service.findById(id);
        if (advertisement == null) {
            return "redirect:/";
        }
        model.addAttribute("advertisement", advertisement);
        return "advertisement_details";
    }

    @GetMapping("/delete/{id}")
    public String deleteAdvertisement(@PathVariable("id") Long id) {
        service.deleteById(id);
        return "redirect:/?success";
    }
}


