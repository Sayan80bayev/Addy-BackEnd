package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.dto.UserDTO;
import com.example.backend.model.User;
import com.example.backend.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private UserService uService;

    @GetMapping("/get/getUser")
    public ResponseEntity<?> getUser(@RequestParam String email) {
        return ResponseEntity.ok().body(uService.entityToDto(uService.findByEmail(email)));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestParam String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();
        if (!email.equals(currentUser.getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("NO_ACCESS");
        }
        try {
            uService.deleteUser(currentUser);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("ERROR");
        }
        return ResponseEntity.ok().body("SUCCESS");
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestPart("user") UserDTO uDto,
            @RequestPart("avatar") MultipartFile avatar) {
        User user = uService.findByEmail(uDto.getEmail());
        if (!uDto.getEmail().equals(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("NO_ACCESS");
        }

        try {
            // Check if current password matches the one stored in the database
            if (uDto.getPassword() != null && !passwordEncoder.matches(uDto.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Incorrect current password");
            }

            // Check if the new password is null when the old password is provided
            if (uDto.getNewPassword() == null && uDto.getPassword() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("New password cannot be null if the old password is provided");
            }

            // Check if the new password is different from the old one
            if (uDto.getNewPassword() != null && !uDto.getNewPassword().isEmpty() &&
                    uDto.getNewPassword().equals(uDto.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("New password must be different from the old one");
            }

            // Check if there are any changes to update
            if (uDto.getName().equals(user.getName()) &&
                    (avatar == null || avatar.isEmpty()) &&
                    (uDto.getNewPassword() == null
                            || passwordEncoder.matches(uDto.getNewPassword(), user.getPassword()))) {
                return ResponseEntity.ok().body("No changes to update");
            }

            // Update user details
            if (!uDto.getName().equals(user.getName())) {
                user.setName(uDto.getName());
            }

            if (avatar != null && !avatar.isEmpty()) {
                user.setAvatar(avatar.getBytes());
            }

            if (uDto.getNewPassword() != null && !uDto.getNewPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(uDto.getNewPassword()));
            }

            uService.saveUser(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok().body("SUCCESS");
    }

}
