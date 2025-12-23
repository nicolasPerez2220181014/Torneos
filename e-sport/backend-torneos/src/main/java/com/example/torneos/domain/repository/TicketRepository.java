package com.example.torneos.domain.repository;

import com.example.torneos.domain.model.Ticket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository {
    Ticket save(Ticket ticket);
    Optional<Ticket> findById(UUID id);
    Optional<Ticket> findByAccessCode(String accessCode);
    List<Ticket> findByOrderId(UUID orderId);
    List<Ticket> findByTournamentId(UUID tournamentId);
    List<Ticket> findByUserId(UUID userId);
    List<Ticket> findByTournamentIdAndUserId(UUID tournamentId, UUID userId);
    void deleteById(UUID id);
    boolean existsByAccessCode(String accessCode);
}