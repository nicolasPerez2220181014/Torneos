package com.example.torneos.infrastructure.persistence.repository;

import com.example.torneos.domain.model.StreamLinkControl;
import com.example.torneos.domain.repository.StreamLinkControlRepository;
import com.example.torneos.infrastructure.persistence.mapper.StreamLinkControlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class StreamLinkControlRepositoryImpl implements StreamLinkControlRepository {

    @Autowired
    private JpaStreamLinkControlRepository jpaRepository;

    @Autowired
    private StreamLinkControlMapper mapper;

    @Override
    public StreamLinkControl save(StreamLinkControl streamLinkControl) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(streamLinkControl)));
    }

    @Override
    public Optional<StreamLinkControl> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<StreamLinkControl> findByTournamentId(UUID tournamentId) {
        return jpaRepository.findByTournamentId(tournamentId).map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}