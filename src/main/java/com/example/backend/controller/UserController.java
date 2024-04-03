package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.model.User;
import com.example.backend.service.UserService;

import jakarta.servlet.http.HttpSession;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService service;
    @Autowired
    private HttpSession session;

    @PostMapping("/registration")
    public ResponseEntity<String> saveUser(@RequestBody User s) {

        boolean studentByEmail = service.getUserByEmail(s.getEmail());
        if (studentByEmail) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        } else {
            boolean saveStudent = service.saveUser(s);
            if (saveStudent) {
                return ResponseEntity.ok().body("Registration successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occured.. Please try again");
            }
        }
    }

    @PostMapping("/login")
    public ResponseEntity<User> loginHandler(@RequestBody User user) {
        User auth = service.checkLogin(user);
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } else {
            session.setAttribute("user", auth.getId());
            return ResponseEntity.ok().body(auth);
        }
    }

    @GetMapping("/userId")
    public ResponseEntity<Long> getUserId() {
        return ResponseEntity.ok((Long) session.getAttribute("user"));
    }
}
