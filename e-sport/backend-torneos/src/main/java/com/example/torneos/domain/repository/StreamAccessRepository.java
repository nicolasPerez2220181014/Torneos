package com.example.torneos.domain.repository;

import com.example.torneos.domain.model.StreamAccess;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StreamAccessRepository {
    StreamAccess save(StreamAccess streamAccess);
    Optional<StreamAccess> findById(UUID id);
    List<StreamAccess> findByTournamentId(UUID tournamentId);
    List<StreamAccess> findByUserId(UUID userId);
    int countFreeAccessByUserId(UUID userId);
    boolean existsByTournamentIdAndUserId(UUID tournamentId, UUID userId);
    Optional<StreamAccess> findByTournamentIdAndUserId(UUID tournamentId, UUID userId);
}