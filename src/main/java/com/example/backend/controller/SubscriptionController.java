package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.SubcribeRequest;
import com.example.backend.model.Advertisement;
import com.example.backend.model.User;
import com.example.backend.model.UserSubscription;
import com.example.backend.service.AdvertisementService;
import com.example.backend.service.SubscriptionService;
import com.example.backend.service.UserService;

@RestController
@RequestMapping("/subs")
public class SubscriptionController {
    @Autowired
    private SubscriptionService service;
    @Autowired
    private AdvertisementService aService;
    @Autowired
    private UserService uService;

    @PostMapping
    public ResponseEntity<?> subscribe(@RequestBody SubcribeRequest s) {
        Advertisement a = aService.findById(s.getId());
        User u = uService.findByEmail(s.getEmail());
        try {
            service.save(UserSubscription.builder().user(u).ad(a).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("ERROR");
        }
        return ResponseEntity.ok().body("SUCCESS");
    }

    @DeleteMapping
    public ResponseEntity<?> unSubscribe(@RequestBody SubcribeRequest s) {
        Advertisement a = aService.findById(s.getId());
        User u = uService.findByEmail(s.getEmail());
        try {
            service.deleteSubs(u.getId(), a.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body("SUCCESS");
    }

    @GetMapping
    public ResponseEntity<?> getSubscribtion() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok().body(service.getSubcribtionsByUserID(currentUser.getId()));
    }
}
