package com.smartcity.backend;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 🔐 Secret key (must be 32+ chars)
    private static final String SECRET = "mysecretkeymysecretkeymysecretkey";

    // ✅ Signing key
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    // ================= GENERATE TOKEN =================
    public String generateToken(String username, String role) {

        return Jwts.builder()
                .setSubject(username)                // username
                .claim("role", role)                 // 🔥 role (important)
                .setIssuedAt(new Date())             // issued time
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ================= EXTRACT DATA =================

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    // ================= CORE CLAIM PARSER =================

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ================= VALIDATION =================

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, String username) {
        try {
            String extractedUsername = extractUsername(token);

            return extractedUsername.equals(username)
                    && !isTokenExpired(token);

        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("❌ JWT ERROR → " + e.getMessage());
            return false;
        }
    }

    // ================= GET KEY =================

    public Key getKey() {
        return key;
    }
}