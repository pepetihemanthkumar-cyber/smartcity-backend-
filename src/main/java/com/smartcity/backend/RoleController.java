package com.smartcity.backend;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role")   // ✅ no conflict path
@CrossOrigin("*")
public class RoleController {

    // 🔐 ADMIN ONLY
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String admin() {
        return "Welcome ADMIN 👑";
    }

    // 🔐 USER + ADMIN
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String user() {
        return "Welcome USER 👤";
    }

    // 🔐 ANY AUTHENTICATED USER
    @GetMapping("/test")
    public String test() {
        return "JWT is working 🚀";
    }
}