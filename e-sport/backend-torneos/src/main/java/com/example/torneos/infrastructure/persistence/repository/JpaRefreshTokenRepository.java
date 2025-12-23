package com.example.torneos.infrastructure.persistence.repository;

import com.example.torneos.infrastructure.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaRefreshTokenRepository extends JpaRepository<RefreshTokenEntity, UUID> {
    
    @Query("SELECT r FROM RefreshTokenEntity r WHERE r.token = :token AND r.expiresAt > :now AND r.revoked = false")
    Optional<RefreshTokenEntity> findByTokenAndNotExpired(@Param("token") String token, @Param("now") Instant now);
    
    void deleteByUserEmail(String userEmail);
}