package com.smartcity.backend;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // 🔥🔥🔥 BYPASS PUBLIC ROUTES (VERY IMPORTANT)
        if (
                path.equals("/") ||
                path.equals("/test") ||
                path.equals("/api/test") ||
                path.startsWith("/api/auth") ||     // login/register
                path.startsWith("/api/upload") ||   // ✅ FIX ADDED
                path.startsWith("/uploads") ||      // ✅ FIX ADDED
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        try {
            if (header != null && header.startsWith("Bearer ")) {

                String token = header.substring(7);

                String username = jwtUtil.extractUsername(token);

                if (username != null &&
                        SecurityContextHolder.getContext().getAuthentication() == null) {

                    if (jwtUtil.validateToken(token, username)) {

                        Optional<User> userOpt = userRepository.findByUsername(username);

                        if (userOpt.isPresent()) {

                            User user = userOpt.get();

                            String role = user.getRole();

                            SimpleGrantedAuthority authority =
                                    new SimpleGrantedAuthority("ROLE_" + role);

                            UsernamePasswordAuthenticationToken auth =
                                    new UsernamePasswordAuthenticationToken(
                                            user.getUsername(),
                                            null,
                                            Collections.singletonList(authority)
                                    );

                            auth.setDetails(
                                    new WebAuthenticationDetailsSource().buildDetails(request)
                            );

                            SecurityContextHolder.getContext().setAuthentication(auth);

                            System.out.println("✅ AUTH SUCCESS → " + username + " ROLE: " + role);
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("❌ JWT ERROR → " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}