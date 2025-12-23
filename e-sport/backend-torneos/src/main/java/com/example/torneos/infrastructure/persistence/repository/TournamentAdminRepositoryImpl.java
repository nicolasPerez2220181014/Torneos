package com.example.torneos.infrastructure.persistence.repository;

import com.example.torneos.domain.model.TournamentAdmin;
import com.example.torneos.domain.repository.TournamentAdminRepository;
import com.example.torneos.infrastructure.persistence.mapper.TournamentAdminMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TournamentAdminRepositoryImpl implements TournamentAdminRepository {

    private final JpaTournamentAdminRepository jpaRepository;
    private final TournamentAdminMapper mapper;

    public TournamentAdminRepositoryImpl(JpaTournamentAdminRepository jpaRepository, TournamentAdminMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public TournamentAdmin save(TournamentAdmin tournamentAdmin) {
        var entity = mapper.toEntity(tournamentAdmin);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<TournamentAdmin> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<TournamentAdmin> findByTournamentId(UUID tournamentId) {
        return jpaRepository.findByTournamentId(tournamentId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<TournamentAdmin> findBySubAdminUserId(UUID subAdminUserId) {
        return jpaRepository.findBySubAdminUserId(subAdminUserId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByTournamentIdAndSubAdminUserId(UUID tournamentId, UUID subAdminUserId) {
        return jpaRepository.existsByTournamentIdAndSubAdminUserId(tournamentId, subAdminUserId);
    }

    @Override
    public long countByTournamentId(UUID tournamentId) {
        return jpaRepository.countByTournamentId(tournamentId);
    }
}