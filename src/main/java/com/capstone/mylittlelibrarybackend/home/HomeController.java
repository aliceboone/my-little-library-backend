package com.capstone.mylittlelibrarybackend.home;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Welcome to your Library!";
    }

    @GetMapping("/login")
    public String login() {
        return "Login to your Library!";
    }
}
