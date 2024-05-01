package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.User;
import com.example.backend.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService uService;

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();
        if (id != currentUser.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You don't have permission to edit this advertisement.");
        }
        try {
            uService.deleteUser(id);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("ERROR");
        }
        return ResponseEntity.ok().body("SUCCESS");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id) {
        // TODO realisation
        return ResponseEntity.ok().body("SUCCESS");
    }
}
