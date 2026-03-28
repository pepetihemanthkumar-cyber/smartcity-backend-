package com.smartcity.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Smart City Backend is LIVE 🚀";
    }

    // 👉 ADD HERE
    @GetMapping("/test")
    public String test() {
        return "API Working ✅";
    }
}