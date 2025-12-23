package com.example.torneos.application.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private UUID userId;
    private String email;
    private String role;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("mySecretKeyForTorneosAppThatIsLongEnoughForHS256Algorithm", 3600000, 86400000);
        userId = UUID.randomUUID();
        email = "test@example.com";
        role = "USER";
    }

    @Test
    void generateToken_Success() {
        // When
        String token = jwtService.generateToken(userId, email, role);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void extractClaims_Success() {
        // Given
        String token = jwtService.generateToken(userId, email, role);

        // When
        Claims claims = jwtService.extractClaims(token);

        // Then
        assertEquals(userId.toString(), claims.getSubject());
        assertEquals(email, claims.get("email"));
        assertEquals(role, claims.get("role"));
    }

    @Test
    void extractUserId_Success() {
        // Given
        String token = jwtService.generateToken(userId, email, role);

        // When
        UUID extractedUserId = jwtService.extractUserId(token);

        // Then
        assertEquals(userId, extractedUserId);
    }

    @Test
    void extractEmail_Success() {
        // Given
        String token = jwtService.generateToken(userId, email, role);

        // When
        String extractedEmail = jwtService.extractEmail(token);

        // Then
        assertEquals(email, extractedEmail);
    }

    @Test
    void extractRole_Success() {
        // Given
        String token = jwtService.generateToken(userId, email, role);

        // When
        String extractedRole = jwtService.extractRole(token);

        // Then
        assertEquals(role, extractedRole);
    }

    @Test
    void isTokenValid_ValidToken_ReturnsTrue() {
        // Given
        String token = jwtService.generateToken(userId, email, role);

        // When
        boolean isValid = jwtService.isTokenValid(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_InvalidToken_ReturnsFalse() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean isValid = jwtService.isTokenValid(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void generateRefreshToken_Success() {
        // When
        String refreshToken = jwtService.generateRefreshToken(userId.toString());

        // Then
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
        assertTrue(jwtService.isTokenValid(refreshToken));
        assertEquals(userId, jwtService.extractUserId(refreshToken));
    }
}