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

        String header = request.getHeader("Authorization");

        try {
            // ✅ Check header exists & starts with Bearer
            if (header != null && header.startsWith("Bearer ")) {

                String token = header.substring(7);

                // ✅ Extract username from token
                String username = jwtUtil.extractUsername(token);

                // ✅ Only authenticate if not already authenticated
                if (username != null &&
                        SecurityContextHolder.getContext().getAuthentication() == null) {

                    // ✅ Validate token
                    if (jwtUtil.validateToken(token, username)) {

                        Optional<User> userOpt = userRepository.findByUsername(username);

                        if (userOpt.isPresent()) {

                            User user = userOpt.get();

                            // ✅ Role handling (VERY IMPORTANT)
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

                            // ✅ Set authentication in context
                            SecurityContextHolder.getContext().setAuthentication(auth);

                            System.out.println("✅ AUTH SUCCESS → " + username + " ROLE: " + role);
                        }
                    }
                }
            }

        } catch (Exception e) {
            // ❌ Do NOT crash server
            System.out.println("❌ JWT ERROR → " + e.getMessage());
        }

        // ✅ Continue filter chain ALWAYS
        filterChain.doFilter(request, response);
    }
}