package com.example.torneos.infrastructure.persistence.repository;

import com.example.torneos.infrastructure.persistence.entity.TicketOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaTicketOrderRepository extends JpaRepository<TicketOrderEntity, UUID> {
    List<TicketOrderEntity> findByTournamentId(UUID tournamentId);
    List<TicketOrderEntity> findByUserId(UUID userId);
    List<TicketOrderEntity> findByStageId(UUID stageId);
    long countByStageIdAndStatus(UUID stageId, TicketOrderEntity.OrderStatus status);
}