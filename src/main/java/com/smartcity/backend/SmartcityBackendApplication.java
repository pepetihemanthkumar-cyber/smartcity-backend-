package com.smartcity.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

// 🔥 ADD THIS IMPORT
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableMethodSecurity   // ✅ already correct
public class SmartcityBackendApplication {

    public static void main(String[] args) {

        // 🔥 TEMPORARY: generate encrypted password
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode("Phk@1176");
        System.out.println("🔐 ENCRYPTED PASSWORD: " + encoded);

        SpringApplication.run(SmartcityBackendApplication.class, args);
    }
}