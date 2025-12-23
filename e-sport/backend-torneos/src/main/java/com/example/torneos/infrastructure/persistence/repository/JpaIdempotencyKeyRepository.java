package com.example.torneos.infrastructure.persistence.repository;

import com.example.torneos.infrastructure.persistence.entity.IdempotencyKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaIdempotencyKeyRepository extends JpaRepository<IdempotencyKeyEntity, UUID> {
    
    @Query("SELECT i FROM IdempotencyKeyEntity i WHERE i.key = :key AND i.expiresAt > :now")
    Optional<IdempotencyKeyEntity> findByKeyAndNotExpired(@Param("key") String key, @Param("now") Instant now);
}