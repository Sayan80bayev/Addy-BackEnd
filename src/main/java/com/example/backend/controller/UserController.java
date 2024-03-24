package com.example.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.backend.model.User;
import com.example.backend.service.UserService;

import jakarta.servlet.http.HttpSession;
@Controller
public class UserController {

    @Autowired
    private UserService service;
    @Autowired
    private HttpSession session;


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
    @GetMapping("/login")
    public String showLogin(Model model){
        model.addAttribute("user", new User());
        return "login";
    }
    @PostMapping("/login")
    public String loginHandler(@ModelAttribute User u, Model model){
        User auth = service.checkLogin(u);
        if(auth == null){
            model.addAttribute("errmsg", "Incorrect conditials");
            return "login";
        }else{
            session.setAttribute("user", (Long)auth.getId());
            return "redirect:/";
        }   
    }
    @GetMapping("/logout")
    public String lougout(){
        session.invalidate();
        return "redirect:/login?out";
    }
}
