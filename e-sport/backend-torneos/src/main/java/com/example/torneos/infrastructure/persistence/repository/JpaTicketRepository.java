package com.example.torneos.infrastructure.persistence.repository;

import com.example.torneos.infrastructure.persistence.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaTicketRepository extends JpaRepository<TicketEntity, UUID> {
    Optional<TicketEntity> findByAccessCode(String accessCode);
    List<TicketEntity> findByOrderId(UUID orderId);
    List<TicketEntity> findByTournamentId(UUID tournamentId);
    List<TicketEntity> findByUserId(UUID userId);
    List<TicketEntity> findByTournamentIdAndUserId(UUID tournamentId, UUID userId);
    boolean existsByAccessCode(String accessCode);
}