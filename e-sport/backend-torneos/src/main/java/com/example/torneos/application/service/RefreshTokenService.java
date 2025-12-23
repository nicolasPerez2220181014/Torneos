package com.example.torneos.application.service;

import com.example.torneos.infrastructure.persistence.entity.RefreshTokenEntity;
import com.example.torneos.infrastructure.persistence.repository.JpaRefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class RefreshTokenService {
    
    private final JpaRefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    
    public RefreshTokenService(JpaRefreshTokenRepository refreshTokenRepository, JwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }
    
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        // 1. Validate refresh token
        RefreshTokenEntity tokenEntity = refreshTokenRepository
            .findByTokenAndNotExpired(refreshToken, Instant.now())
            .orElseThrow(() -> new IllegalArgumentException("Invalid or expired refresh token"));
        
        // 2. Blacklist old token
        tokenEntity.setRevoked(true);
        refreshTokenRepository.save(tokenEntity);
        
        // 3. Generate new tokens
        String newAccessToken = jwtService.generateAccessToken(tokenEntity.getUserEmail());
        String newRefreshToken = jwtService.generateRefreshToken(tokenEntity.getUserEmail());
        
        // 4. Store new refresh token
        storeRefreshToken(newRefreshToken, tokenEntity.getUserEmail());
        
        return new AuthResponse(newAccessToken, newRefreshToken);
    }
    
    @Transactional
    public void storeRefreshToken(String token, String userEmail) {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setToken(token);
        entity.setUserEmail(userEmail);
        entity.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
        
        refreshTokenRepository.save(entity);
    }
    
    @Transactional
    public void revokeAllUserTokens(String userEmail) {
        refreshTokenRepository.deleteByUserEmail(userEmail);
    }
    
    public static class AuthResponse {
        private final String accessToken;
        private final String refreshToken;
        
        public AuthResponse(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
        
        public String getAccessToken() { return accessToken; }
        public String getRefreshToken() { return refreshToken; }
    }
}