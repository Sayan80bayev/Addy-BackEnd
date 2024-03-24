package com.example.backend.controller;

import com.example.backend.model.Advertisement;
import com.example.backend.model.User;
import com.example.backend.service.AdvertisementService;
import com.example.backend.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdvertisementController {
    @Autowired
    private AdvertisementService service;
    @Autowired
    private UserService userService;
    @Autowired 
    private HttpSession session;

    @GetMapping("/new")
    public String showNewAdvertisementForm(Model model) {
        if(userService.checkAuth()){
            Advertisement advertisement = new Advertisement();
            model.addAttribute("advertisement", advertisement);
            model.addAttribute("user_id", (Long) session.getAttribute("user"));
            return "new_advertisement";
        }else
            return "redirect:/login?err";
    }

    @PostMapping("/save")
    public String saveAdvertisement(@ModelAttribute("advertisement") Advertisement advertisement) {
        service.save(advertisement);
        return "redirect:/";
    }


    @GetMapping("/edit/{id}")
    public String showEditAdvertisementForm(@PathVariable("id") Long id, Model model) {
        if(userService.checkAuth()){
            Advertisement advertisement = service.findById(id);
            if (advertisement.getUser().getId() == session.getAttribute("user")) {
                model.addAttribute("advertisement", advertisement);
                model.addAttribute("user_id", (Long) session.getAttribute("user"));
                return "edit_advertisement";
            }else{
                return "redirect:/?acsErr";
            }
        }else
            return "redirect:/login?err";
    }
    @GetMapping("/view/{id}")
    public String viewAdvertisement(@PathVariable("id") Long id, Model model) {
        Advertisement advertisement = service.findById(id);
        if (advertisement == null) {
            return "redirect:/";
        }
        model.addAttribute("advertisement", advertisement);
        model.addAttribute("user_id", (Long) session.getAttribute("user"));
        return "advertisement_details";
    }

    @PostMapping("/delete")
    public String deleteAdvertisement(@RequestParam("add_id") Long id) {
        if(userService.checkAuth()){
            Advertisement advertisement = service.findById(id);
            User user = advertisement.getUser();
            if ( user.getId() == (Long) session.getAttribute("user")) {
                service.deleteById(id);
                return "redirect:/?success";
            }else{
                return "redirect:/?acsErr";
            }
        }else{
            return "redirect:/login?err";
        }
    }
}


