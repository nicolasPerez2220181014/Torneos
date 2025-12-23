package com.example.torneos.domain.repository;

import com.example.torneos.domain.model.TicketSaleStage;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketSaleStageRepository {
    TicketSaleStage save(TicketSaleStage stage);
    Optional<TicketSaleStage> findById(UUID id);
    Optional<TicketSaleStage> findByIdForUpdate(UUID id);
    List<TicketSaleStage> findByTournamentId(UUID tournamentId);
    List<TicketSaleStage> findByTournamentIdAndActive(UUID tournamentId, boolean active);
    void deleteById(UUID id);
    boolean existsByTournamentIdAndStageType(UUID tournamentId, TicketSaleStage.StageType stageType);
}