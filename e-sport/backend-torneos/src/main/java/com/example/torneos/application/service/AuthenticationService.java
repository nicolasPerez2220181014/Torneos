package com.example.torneos.application.service;

import com.example.torneos.domain.model.User;
import com.example.torneos.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuditLogService auditLogService;

    public AuthenticationService(UserRepository userRepository, 
                               JwtService jwtService,
                               RefreshTokenService refreshTokenService,
                               AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.auditLogService = auditLogService;
    }

    public AuthResponse login(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String accessToken = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        
        // Store refresh token
        refreshTokenService.storeRefreshToken(refreshToken, user.getEmail());

        // Auditoría
        auditLogService.logEvent(
            com.example.torneos.domain.model.AuditLog.EventType.USER_CREATED,
            com.example.torneos.domain.model.AuditLog.EntityType.USER,
            user.getId(),
            user.getId(),
            String.format("Usuario '%s' inició sesión", user.getEmail())
        );

        UserInfo userInfo = new UserInfo(user.getId(), user.getEmail(), user.getFullName(), user.getRole().name(), 
            user.getCreatedAt() != null ? user.getCreatedAt().toString() : "",
            user.getUpdatedAt() != null ? user.getUpdatedAt().toString() : "");
        return new AuthResponse(accessToken, refreshToken, "Bearer", 3600, userInfo);
    }

    public RefreshTokenService.AuthResponse refreshToken(String refreshToken) {
        return refreshTokenService.refreshToken(refreshToken);
    }
    
    public void logout(String userEmail) {
        refreshTokenService.revokeAllUserTokens(userEmail);
    }

    @Transactional(readOnly = true)
    public User validateToken(String token) {
        if (!jwtService.isTokenValid(token) || jwtService.isTokenExpired(token)) {
            throw new IllegalArgumentException("Token inválido o expirado");
        }

        UUID userId = jwtService.extractUserId(token);
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    public static class UserInfo {
        private final UUID id;
        private final String email;
        private final String fullName;
        private final String role;
        private final String createdAt;
        private final String updatedAt;

        public UserInfo(UUID id, String email, String fullName, String role, String createdAt, String updatedAt) {
            this.id = id;
            this.email = email;
            this.fullName = fullName;
            this.role = role;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public UUID getId() { return id; }
        public String getEmail() { return email; }
        public String getFullName() { return fullName; }
        public String getRole() { return role; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }
    }

    public static class AuthResponse {
        private final String accessToken;
        private final String refreshToken;
        private final String tokenType;
        private final int expiresIn;
        private final UserInfo user;

        public AuthResponse(String accessToken, String refreshToken, String tokenType, int expiresIn, UserInfo user) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.tokenType = tokenType;
            this.expiresIn = expiresIn;
            this.user = user;
        }

        public String getAccessToken() { return accessToken; }
        public String getRefreshToken() { return refreshToken; }
        public String getTokenType() { return tokenType; }
        public int getExpiresIn() { return expiresIn; }
        public UserInfo getUser() { return user; }
    }
}