package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.backend.dto.UserDTO;
import com.example.backend.controller.facades.UserFacade;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserFacade facade;

    @GetMapping("/get")
    public ResponseEntity<?> getUser(@RequestParam String email) {
        return facade.getUser(email);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestParam String email) {
        return facade.deleteUser(email);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestPart("user") UserDTO userDTO,
            @RequestPart("avatar") MultipartFile avatar) {
        return facade.updateUser(userDTO, avatar);
    }
}
