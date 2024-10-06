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

import com.example.backend.dto.request.SubscribeRequest;
import com.example.backend.model.User;
import com.example.backend.service.SubscriptionService;

@RestController
@RequestMapping("/subs")
public class SubscriptionController {
    @Autowired
    private SubscriptionService service;

    @PostMapping
    public ResponseEntity<?> subscribe(@RequestBody SubscribeRequest s) {
        service.subscribeUser(s);

        return ResponseEntity.ok().body("SUCCESS");
    }

    @DeleteMapping
    public ResponseEntity<?> unSubscribe(@RequestBody SubscribeRequest s) {
        service.deleteSubs(s);
        return ResponseEntity.ok().body("SUCCESS");
    }

    @GetMapping
    public ResponseEntity<?> getSubscribtion() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        return ResponseEntity.ok().body(service.getSubcribtionsByUserID(currentUser.getId()));
    }
}
