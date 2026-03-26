package com.example.torneos.infrastructure.security;

import com.example.torneos.domain.model.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Ejemplos de generación de tokens JWT
 */
@Component
public class TokenGenerationExample {

    private final JwtUtil jwtUtil;

    public TokenGenerationExample(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Ejemplo 1: Generar token desde entidad User
     */
    public String generateTokenFromUser(User user) {
        return jwtUtil.generateToken(user);
    }

    /**
     * Ejemplo 2: Generar token con datos individuales
     */
    public String generateTokenFromData(UUID userId, String email, String role) {
        return jwtUtil.generateToken(userId, email, role);
    }

    /**
     * Ejemplo 3: Generar token para ORGANIZER
     */
    public String generateOrganizerToken() {
        UUID userId = UUID.randomUUID();
        return jwtUtil.generateToken(userId, "organizer@example.com", "ORGANIZER");
    }

    /**
     * Ejemplo 4: Generar token para USER
     */
    public String generateUserToken() {
        UUID userId = UUID.randomUUID();
        return jwtUtil.generateToken(userId, "user@example.com", "USER");
    }

    /**
     * Ejemplo 5: Generar token para SUBADMIN
     */
    public String generateSubAdminToken() {
        UUID userId = UUID.randomUUID();
        return jwtUtil.generateToken(userId, "subadmin@example.com", "SUBADMIN");
    }

    /**
     * Uso en AuthService o similar:
     * 
     * @Autowired
     * private TokenGenerationExample tokenExample;
     * 
     * public String login(String email, String password) {
     *     User user = authenticate(email, password);
     *     return tokenExample.generateTokenFromUser(user);
     * }
     */
}
