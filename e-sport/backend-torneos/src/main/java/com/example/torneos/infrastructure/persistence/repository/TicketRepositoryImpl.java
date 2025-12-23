package com.example.torneos.infrastructure.persistence.repository;

import com.example.torneos.domain.model.Ticket;
import com.example.torneos.domain.repository.TicketRepository;
import com.example.torneos.infrastructure.persistence.mapper.TicketMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TicketRepositoryImpl implements TicketRepository {

    private final JpaTicketRepository jpaRepository;
    private final TicketMapper mapper;

    public TicketRepositoryImpl(JpaTicketRepository jpaRepository, TicketMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Ticket save(Ticket ticket) {
        var entity = mapper.toEntity(ticket);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Ticket> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Ticket> findByAccessCode(String accessCode) {
        return jpaRepository.findByAccessCode(accessCode).map(mapper::toDomain);
    }

    @Override
    public List<Ticket> findByOrderId(UUID orderId) {
        return jpaRepository.findByOrderId(orderId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Ticket> findByTournamentId(UUID tournamentId) {
        return jpaRepository.findByTournamentId(tournamentId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Ticket> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Ticket> findByTournamentIdAndUserId(UUID tournamentId, UUID userId) {
        return jpaRepository.findByTournamentIdAndUserId(tournamentId, userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByAccessCode(String accessCode) {
        return jpaRepository.existsByAccessCode(accessCode);
    }
}