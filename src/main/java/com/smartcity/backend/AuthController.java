package com.smartcity.backend;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // ✅ allow frontend (React)
public class AuthController {

    private final UserRepository repo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // ✅ Constructor Injection
    public AuthController(UserRepository repo, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    // ================= REGISTER =================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        if (user.getUsername() == null || user.getPassword() == null) {
            return ResponseEntity.badRequest().body("Username and password required ❌");
        }

        Optional<User> existingUser = repo.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists ❌");
        }

        // 🔐 Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 👤 Default role
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        repo.save(user);

        return ResponseEntity.ok("User registered successfully ✅");
    }

    // ================= LOGIN =================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        if (request.getUsername() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body("Username and password required ❌");
        }

        Optional<User> userOpt = repo.findByUsername(request.getUsername());

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found ❌");
        }

        User existing = userOpt.get();

        // ❌ Wrong password
        if (!passwordEncoder.matches(request.getPassword(), existing.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid password ❌");
        }

        // 🔐 Generate JWT (IMPORTANT: include role)
        String token = jwtUtil.generateToken(
                existing.getUsername(),
                existing.getRole()
        );

        return ResponseEntity.ok(new AuthResponse(token));
    }
}