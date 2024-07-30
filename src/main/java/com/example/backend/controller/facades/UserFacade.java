package com.example.backend.controller.facades;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;

import com.example.backend.dto.AdvertisementDTO;
import com.example.backend.dto.UserDTO;
import com.example.backend.model.Advertisement;
import com.example.backend.model.User;
import com.example.backend.service.AdvertisementService;
import com.example.backend.service.UserService;

@Component
public class UserFacade {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private AdvertisementService aService;

    public ResponseEntity<?> getUser(String email) {
        UserDTO userDTO = userService.entityToDto(userService.findByEmail(email));
        return ResponseEntity.ok().body(userDTO);
    }

    public ResponseEntity<?> deleteUser(String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (!email.equals(currentUser.getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("NO_ACCESS");
        }

        try {
            userService.deleteUser(currentUser);
            return ResponseEntity.ok().body("SUCCESS");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR");
        }
    }

    public ResponseEntity<?> updateUser(UserDTO userDTO, MultipartFile avatar) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (!userDTO.getEmail().equals(currentUser.getEmail())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("NO_ACCESS");
        }

        try {
            // Checking password
            if (userDTO.getPassword() != null
                    && !passwordEncoder.matches(userDTO.getPassword(), currentUser.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect current password");
            }

            // Checking new password
            if (userDTO.getNewPassword() == null && userDTO.getPassword() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("New password cannot be null if the old password is provided");
            }

            // Checking new and old password difference
            if (userDTO.getNewPassword() != null && !userDTO.getNewPassword().isEmpty() &&
                    userDTO.getNewPassword().equals(userDTO.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("New password must be different from the old one");
            }

            // Checking for changes
            if (userDTO.getName().equals(currentUser.getName()) &&
                    (avatar == null || avatar.isEmpty()) &&
                    (userDTO.getNewPassword() == null
                            || passwordEncoder.matches(userDTO.getNewPassword(), currentUser.getPassword()))) {
                return ResponseEntity.ok().body("No changes to update");
            }

            // Updating user details
            if (!userDTO.getName().equals(currentUser.getName())) {
                currentUser.setName(userDTO.getName());
            }

            if (avatar != null && !avatar.isEmpty()) {
                currentUser.setAvatar(avatar.getBytes());
            }

            if (userDTO.getNewPassword() != null && !userDTO.getNewPassword().isEmpty()) {
                currentUser.setPassword(passwordEncoder.encode(userDTO.getNewPassword()));
            }

            userService.saveUser(currentUser);
            return ResponseEntity.ok().body("{\"status\":\"SUCCESS\"}");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public ResponseEntity<?> getUserAds(String email) {
        User user = userService.findByEmail(email);
        List<Advertisement> ads = user.getAdvertisements();
        List<AdvertisementDTO> aDtos = ads.stream().map(ad -> aService.mapToDto(ad)).collect(Collectors.toList());
        return ResponseEntity.ok().body(aDtos);
    }
}
