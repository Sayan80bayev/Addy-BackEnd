package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.backend.model.User;
import com.example.backend.service.UserService;
@Controller
public class UserController {
    @Autowired
    private UserService service;
    @GetMapping("/registration")
    public String loadRegistration(Model model){
        model.addAttribute("user", new User());
        return "registration";
    }
    @PostMapping("/registration")
        public String saveUser(@ModelAttribute User s, Model model) {

            boolean studentByEmail = service.getUserByEmail(s.getEmail());
            if (studentByEmail) {
                model.addAttribute("errmsg", "User already exists");
                return "registration";
            } else {
                boolean saveStudent = service.saveUser(s);
                if (saveStudent) {
                    model.addAttribute("sucmsg", "Registration successfully");
                } else {
                    model.addAttribute("errmsg", "Error occured.. Please try again");
                }
                return "registration";
            }
        }
}
