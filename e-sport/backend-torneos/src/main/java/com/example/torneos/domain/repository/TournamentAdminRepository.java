package com.example.torneos.domain.repository;

import com.example.torneos.domain.model.TournamentAdmin;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TournamentAdminRepository {
    TournamentAdmin save(TournamentAdmin tournamentAdmin);
    Optional<TournamentAdmin> findById(UUID id);
    List<TournamentAdmin> findByTournamentId(UUID tournamentId);
    List<TournamentAdmin> findBySubAdminUserId(UUID subAdminUserId);
    void deleteById(UUID id);
    boolean existsByTournamentIdAndSubAdminUserId(UUID tournamentId, UUID subAdminUserId);
    long countByTournamentId(UUID tournamentId);
}