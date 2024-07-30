package com.example.backend.controller;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/get/getUser")
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

    @GetMapping("/getUserAds/{email}")
    public ResponseEntity<?> getUserAds(@PathVariable("email") String email) {
        return facade.getUserAds(email);
    }
}
