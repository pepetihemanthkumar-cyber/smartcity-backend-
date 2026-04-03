package com.smartcity.backend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")   // 🔥 IMPORTANT: base path to avoid conflict
public class TestController {

    // ✅ Protected endpoint
    @GetMapping("/test")
    public String test() {
        return "Protected API Working ✅";
    }
}