package com.example.torneos.domain.repository;

import com.example.torneos.domain.model.TicketOrder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketOrderRepository {
    TicketOrder save(TicketOrder order);
    Optional<TicketOrder> findById(UUID id);
    List<TicketOrder> findByTournamentId(UUID tournamentId);
    List<TicketOrder> findByUserId(UUID userId);
    List<TicketOrder> findByStageId(UUID stageId);
    void deleteById(UUID id);
    long countByStageIdAndStatus(UUID stageId, TicketOrder.OrderStatus status);
}