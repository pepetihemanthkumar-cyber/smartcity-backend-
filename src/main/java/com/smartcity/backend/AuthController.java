package com.smartcity.backend;

import com.smartcity.backend.service.GoogleService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository repo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final GoogleService googleService;

    public AuthController(UserRepository repo,
                          JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder,
                          GoogleService googleService) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.googleService = googleService;
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        if (user.getUsername() == null || user.getPassword() == null) {
            return ResponseEntity.badRequest().body("Username and password required ❌");
        }

        if (repo.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists ❌");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(user.getRole() == null ? "USER" : user.getRole());
        user.setAvatar("");

        repo.save(user);

        return ResponseEntity.ok("User registered successfully ✅");
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        Optional<User> userOpt = repo.findByUsername(request.getUsername());

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found ❌");
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid password ❌");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return ResponseEntity.ok(buildAuthResponse(token, user));
    }

    // GOOGLE LOGIN
    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body) {

        try {
            String token = body.get("token");

            if (token == null || token.isEmpty()) {
                return ResponseEntity.badRequest().body("Token missing ❌");
            }

            GoogleIdToken.Payload payload = googleService.verifyToken(token);

            if (payload == null) {
                return ResponseEntity.badRequest().body("Invalid Google token ❌");
            }

            String email = payload.getEmail();

            Optional<User> userOpt = repo.findByUsername(email);
            User user;

            if (userOpt.isEmpty()) {
                user = new User();
                user.setUsername(email);
                user.setPassword("");
                user.setRole("USER");
                user.setAvatar("");

                repo.save(user);
            } else {
                user = userOpt.get();
            }

            String jwt = jwtUtil.generateToken(user.getUsername(), user.getRole());

            return ResponseEntity.ok(buildAuthResponse(jwt, user));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Google login failed ❌");
        }
    }

    // CURRENT USER
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication auth) {

        if (auth == null) {
            return ResponseEntity.status(401).body("Unauthorized ❌");
        }

        Optional<User> userOpt = repo.findByUsername(auth.getName());

        if (userOpt.isPresent()) {
            return ResponseEntity.ok(buildUserResponse(userOpt.get()));
        } else {
            return ResponseEntity.badRequest().body("User not found ❌");
        }
    }

    // HELPERS
    private Map<String, Object> buildAuthResponse(String token, User user) {
        Map<String, Object> res = new HashMap<>();
        res.put("token", token);
        res.put("username", user.getUsername());
        res.put("role", user.getRole());
        res.put("avatar", user.getAvatar());
        return res;
    }

    private Map<String, Object> buildUserResponse(User user) {
        Map<String, Object> res = new HashMap<>();
        res.put("username", user.getUsername());
        res.put("role", user.getRole());
        res.put("avatar", user.getAvatar());
        return res;
    }
}