package com.example.backend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.response.NotificationResponse;
import com.example.backend.model.Notification;
import com.example.backend.model.User;
import com.example.backend.service.NotificationService;

@RestController
@RequestMapping("/notes")
public class NotificationController {
    @Autowired
    private NotificationService service;

    @GetMapping
    public ResponseEntity<?> getNotesByEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok().body(service.findByEmail(currentUser.getEmail()));
    }

    @PostMapping()
    public ResponseEntity<?> seen(@RequestBody List<NotificationResponse> nDtos) {
        List<Notification> notifications = new ArrayList<>();
        for (int i = 0; i < nDtos.size(); i++) {
            notifications.add(service.findById(nDtos.get(i).getId()));
        }
        try {
            service.seen(notifications);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("SUCCESS");
    }
}
