package com.example.torneos.infrastructure.persistence.repository;

import com.example.torneos.infrastructure.persistence.entity.TournamentAdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaTournamentAdminRepository extends JpaRepository<TournamentAdminEntity, UUID> {
    List<TournamentAdminEntity> findByTournamentId(UUID tournamentId);
    List<TournamentAdminEntity> findBySubAdminUserId(UUID subAdminUserId);
    boolean existsByTournamentIdAndSubAdminUserId(UUID tournamentId, UUID subAdminUserId);
    long countByTournamentId(UUID tournamentId);
}