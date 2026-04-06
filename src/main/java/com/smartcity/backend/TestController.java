package com.smartcity.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")   // base path
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "Backend working 🚀";
    }
}