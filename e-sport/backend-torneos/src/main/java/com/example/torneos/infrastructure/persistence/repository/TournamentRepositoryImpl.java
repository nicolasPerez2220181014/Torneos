package com.example.torneos.infrastructure.persistence.repository;

import com.example.torneos.domain.model.Tournament;
import com.example.torneos.domain.repository.TournamentRepository;
import com.example.torneos.infrastructure.persistence.mapper.TournamentMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TournamentRepositoryImpl implements TournamentRepository {

    private final JpaTournamentRepository jpaRepository;
    private final TournamentMapper mapper;

    public TournamentRepositoryImpl(JpaTournamentRepository jpaRepository, TournamentMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Tournament save(Tournament tournament) {
        var entity = mapper.toEntity(tournament);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Tournament> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<Tournament> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public List<Tournament> findByOrganizerId(UUID organizerId) {
        return jpaRepository.findByOrganizerId(organizerId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Tournament> findByStatus(Tournament.TournamentStatus status) {
        var entityStatus = com.example.torneos.infrastructure.persistence.entity.TournamentEntity.TournamentStatus.valueOf(status.name());
        return jpaRepository.findByStatus(entityStatus).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Page<Tournament> findByFilters(Boolean isPaid, Tournament.TournamentStatus status, 
                                        UUID categoryId, UUID gameTypeId, UUID organizerId, Pageable pageable) {
        var entityStatus = status != null ? 
            com.example.torneos.infrastructure.persistence.entity.TournamentEntity.TournamentStatus.valueOf(status.name()) : null;
        
        return jpaRepository.findByFilters(isPaid, entityStatus, categoryId, gameTypeId, organizerId, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public long countByOrganizerIdAndIsPaidAndStatus(UUID organizerId, boolean isPaid, Tournament.TournamentStatus status) {
        var entityStatus = com.example.torneos.infrastructure.persistence.entity.TournamentEntity.TournamentStatus.valueOf(status.name());
        return jpaRepository.countByOrganizerIdAndIsPaidAndStatus(organizerId, isPaid, entityStatus);
    }
}