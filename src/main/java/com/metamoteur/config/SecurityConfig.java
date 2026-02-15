package com.metamoteur.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuration de sécurité Spring Security
 * Protection contre XSS, CSRF, injection, clickjacking
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // CSRF protection avec cookies
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers(
                                "/api/agent/**",  // Agents ne supportent pas CSRF
                                "/actuator/**"     // Actuator endpoints
                        )
                )

                // Headers de sécurité
                .headers(headers -> headers
                                // Content Security Policy
                                .contentSecurityPolicy(csp -> csp
                                        .policyDirectives(
                                                "default-src 'self'; " +
                                                        "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net; " +
                                                        "style-src 'self' 'unsafe-inline'; " +
                                                        "img-src 'self' data: https:; " +
                                                        "font-src 'self' data:; " +
                                                        "connect-src 'self'; " +
                                                        "frame-ancestors 'none'; " +
                                                        "base-uri 'self'; " +
                                                        "form-action 'self'"
                                        )
                                )

                                // XSS Protection
//                        .xssProtection(xss -> xss
//                                .headerValue("1; mode=block")
//                        )

                                // Clickjacking protection
                                .frameOptions(frame -> frame.deny())

                                // MIME type sniffing protection
                                .contentTypeOptions(contentType -> {
                                })

                                // Referrer Policy
                                .referrerPolicy(referrer -> referrer
                                        .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                                )

                                // HSTS (HTTP Strict Transport Security)
                                .httpStrictTransportSecurity(hsts -> hsts
                                        .includeSubDomains(true)
                                        .maxAgeInSeconds(31536000) // 1 an
                                )
                )

                // Session management
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // API REST stateless
                )

                // Autorisation des endpoints
                .authorizeHttpRequests(auth -> auth
                        // Endpoints publics
                        .requestMatchers(HttpMethod.POST, "/api/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/search/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/agent/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/agent/ping").permitAll()

                        // Analytics publiques (lecture seule)
                        .requestMatchers(HttpMethod.GET, "/api/analytics/**").permitAll()

                        // Actuator
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/actuator/**").hasRole("ADMIN")

                        // Admin endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Swagger/OpenAPI
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**"
                        ).permitAll()

                        // Tout le reste nécessite authentification
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    /**
     * Configuration CORS
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origines autorisées (à configurer via properties en prod)
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:8080"
        ));

        // Méthodes HTTP autorisées
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        // Headers autorisés
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-CSRF-TOKEN",
                "X-Requested-With"
        ));

        // Headers exposés
        configuration.setExposedHeaders(Arrays.asList(
                "X-Total-Count",
                "X-Page-Number"
        ));

        // Credentials
        configuration.setAllowCredentials(true);

        // Max age du preflight cache
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * Encodeur de mots de passe BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Strength 12
    }
}