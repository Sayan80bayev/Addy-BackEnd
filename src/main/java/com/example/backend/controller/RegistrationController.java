package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    @Autowired
    private UserRepo userRepository;

    @GetMapping
    public ModelAndView showRegistrationForm() {
        ModelAndView mav = new ModelAndView("register");
        mav.addObject("user", new User());
        return mav;
    }

    @PostMapping
    public String registerUser(User user) {
        userRepository.save(user);
        return "redirect:/register?success";
    }
}
