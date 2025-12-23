package com.example.torneos.infrastructure.persistence.repository;

import com.example.torneos.domain.model.StreamAccess;
import com.example.torneos.domain.repository.StreamAccessRepository;
import com.example.torneos.infrastructure.persistence.mapper.StreamAccessMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class StreamAccessRepositoryImpl implements StreamAccessRepository {

    @Autowired
    private JpaStreamAccessRepository jpaRepository;

    @Autowired
    private StreamAccessMapper mapper;

    @Override
    public StreamAccess save(StreamAccess streamAccess) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(streamAccess)));
    }

    @Override
    public Optional<StreamAccess> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<StreamAccess> findByTournamentId(UUID tournamentId) {
        return jpaRepository.findByTournamentId(tournamentId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<StreamAccess> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public int countFreeAccessByUserId(UUID userId) {
        return jpaRepository.countFreeAccessByUserId(userId);
    }

    @Override
    public boolean existsByTournamentIdAndUserId(UUID tournamentId, UUID userId) {
        return jpaRepository.existsByTournamentIdAndUserId(tournamentId, userId);
    }

    @Override
    public Optional<StreamAccess> findByTournamentIdAndUserId(UUID tournamentId, UUID userId) {
        return jpaRepository.findByTournamentIdAndUserId(tournamentId, userId).map(mapper::toDomain);
    }
}