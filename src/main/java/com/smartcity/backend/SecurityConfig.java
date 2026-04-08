package com.smartcity.backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    /* ================= MAIN SECURITY ================= */

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // ✅ ENABLE CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // ❌ DISABLE CSRF
            .csrf(AbstractHttpConfigurer::disable)

            // ❌ DISABLE DEFAULT LOGIN
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)

            // ✅ STATELESS (JWT)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 🔥 HANDLE 401 (IMPORTANT)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> {
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                })
            )

            // 🔥 ROUTES CONFIG
            .authorizeHttpRequests(auth -> auth

                // ✅ PUBLIC ROUTES
                .requestMatchers(
                    "/",
                    "/test",
                    "/api/test",

                    "/api/auth/login",
                    "/api/auth/register",
                    "/api/auth/**",

                    "/api/upload",
                    "/uploads/**",

                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()

                // 🔥🔥🔥 IMPORTANT FIX (ALLOW ISSUES)
                .requestMatchers("/api/issues/**").permitAll()

                // 🔒 EVERYTHING ELSE NEEDS AUTH
                .anyRequest().authenticated()
            )

            // 🔥 JWT FILTER
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /* ================= CORS CONFIG ================= */

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // 🔥 ALLOW COOKIES + AUTH
        config.setAllowCredentials(true);

        // 🔥 VERY IMPORTANT (FIX VERCEL ISSUE)
        config.setAllowedOrigins(List.of(
            "http://localhost:3000",
            "http://localhost:5173",
            "https://smartcity-professional.vercel.app" // 🔥 REPLACE IF DIFFERENT
        ));

        // 🔥 ALLOW ALL METHODS
        config.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // 🔥 ALLOW HEADERS
        config.setAllowedHeaders(List.of("*"));

        // 🔥 EXPOSE AUTH HEADER
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    /* ================= PASSWORD ================= */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /* ================= AUTH MANAGER ================= */

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}