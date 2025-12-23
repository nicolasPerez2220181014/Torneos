package com.example.torneos.infrastructure.persistence.repository;

import com.example.torneos.infrastructure.persistence.entity.StreamAccessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaStreamAccessRepository extends JpaRepository<StreamAccessEntity, UUID> {
    List<StreamAccessEntity> findByTournamentId(UUID tournamentId);
    List<StreamAccessEntity> findByUserId(UUID userId);
    
    @Query("SELECT COUNT(s) FROM StreamAccessEntity s WHERE s.userId = :userId AND s.accessType = 'FREE'")
    int countFreeAccessByUserId(@Param("userId") UUID userId);
    
    boolean existsByTournamentIdAndUserId(UUID tournamentId, UUID userId);
    Optional<StreamAccessEntity> findByTournamentIdAndUserId(UUID tournamentId, UUID userId);
}