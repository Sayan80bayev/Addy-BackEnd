package com.example.backend.controller;

import com.example.backend.model.Advertisement;
import com.example.backend.model.User;
import com.example.backend.service.AdvertisementService;
import com.example.backend.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api")
public class AdvertisementController {
    @Autowired
    private AdvertisementService service;
    @Autowired
    private UserService userService;
    @Autowired
    private HttpSession session;

    @PostMapping("/save")
    public ResponseEntity<String> saveAdvertisement(@ModelAttribute("advertisement") Advertisement advertisement,
            HttpSession session) {
        if (!userService.checkAuth()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: You must be logged in to perform this action.");
        }
        Long sessionUser = (Long) session.getAttribute("user");
        if (sessionUser == null || !sessionUser.equals(advertisement.getUser().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Forbidden: You can only save advertisements associated with your account.");
        }

        service.save(advertisement);

        return ResponseEntity.ok("SUCCESS: Advertisement saved successfully.");
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteAdvertisement(@RequestParam("add_id") Long id) {
        if (!userService.checkAuth())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Unauthorized: You must be logged in to perform this action.");
        Advertisement advertisement = service.findById(id);
        User user = advertisement.getUser();
        if (user.getId() != (Long) session.getAttribute("user"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Forbidden: You can only save advertisements associated with your account.");
        service.deleteById(id);
        return ResponseEntity.ok().body("SUCCESS: Successfully deleted");
    }
}
