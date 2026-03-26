package com.example.torneos.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final SecurityExceptionHandler securityExceptionHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, SecurityExceptionHandler securityExceptionHandler) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.securityExceptionHandler = securityExceptionHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(securityExceptionHandler)
                .accessDeniedHandler(securityExceptionHandler)
            )
            .authorizeHttpRequests(authz -> authz
                // Endpoints públicos
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/users").permitAll() // Registro de usuarios
                .requestMatchers("/api/simple/**").permitAll() // Endpoint temporal simple
                .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/game-types/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/tournaments/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**").permitAll()
                
                // Endpoints que requieren autenticación
                .requestMatchers(HttpMethod.POST, "/api/categories/**").hasRole("ORGANIZER")
                .requestMatchers(HttpMethod.PUT, "/api/categories/**").hasRole("ORGANIZER")
                .requestMatchers(HttpMethod.POST, "/api/game-types/**").hasRole("ORGANIZER")
                .requestMatchers(HttpMethod.PUT, "/api/game-types/**").hasRole("ORGANIZER")
                .requestMatchers(HttpMethod.POST, "/api/tournaments/**").permitAll() // Temporal para testing
                .requestMatchers(HttpMethod.PUT, "/api/tournaments/**").permitAll() // Temporal para testing
                .requestMatchers("/api/tournaments/*/subadmins/**").hasRole("ORGANIZER")
                .requestMatchers("/api/tournaments/*/stages/**").hasRole("ORGANIZER")
                .requestMatchers("/api/tournaments/*/stream/url").hasRole("ORGANIZER")
                .requestMatchers("/api/tournaments/*/stream/block").hasRole("ORGANIZER")
                .requestMatchers("/api/tournaments/*/stream/unblock").hasRole("ORGANIZER")
                .requestMatchers("/api/audit-logs/**").hasAnyRole("ORGANIZER", "SUBADMIN")
                
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}