package com.example.demo.controller;

import com.example.demo.model.AppUser;
import com.example.demo.service.CustomUserDetailService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final CustomUserDetailService userDetailService;

    public AuthController(CustomUserDetailService userDetailService) {
        this.userDetailService = userDetailService;
    }

    @GetMapping("/login")
    public String showLoginForm(){
        return "login";
    }

    @PostMapping("/logout")
    public String logoutUser(){
        return "redirect:/login?logout";
    }

    @GetMapping("/register")
    public String showRegistrationForm(){
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute AppUser appUser){
        userDetailService.addUser(appUser);
        return "redirect:/login";
    }
}
