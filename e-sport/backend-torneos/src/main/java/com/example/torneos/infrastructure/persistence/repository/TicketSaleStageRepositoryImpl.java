package com.example.torneos.infrastructure.persistence.repository;

import com.example.torneos.domain.model.TicketSaleStage;
import com.example.torneos.domain.repository.TicketSaleStageRepository;
import com.example.torneos.infrastructure.persistence.mapper.TicketSaleStageMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TicketSaleStageRepositoryImpl implements TicketSaleStageRepository {

    private final JpaTicketSaleStageRepository jpaRepository;
    private final TicketSaleStageMapper mapper;

    public TicketSaleStageRepositoryImpl(JpaTicketSaleStageRepository jpaRepository, TicketSaleStageMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public TicketSaleStage save(TicketSaleStage stage) {
        var entity = mapper.toEntity(stage);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<TicketSaleStage> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<TicketSaleStage> findByIdForUpdate(UUID id) {
        return jpaRepository.findByIdForUpdate(id).map(mapper::toDomain);
    }

    @Override
    public List<TicketSaleStage> findByTournamentId(UUID tournamentId) {
        return jpaRepository.findByTournamentId(tournamentId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<TicketSaleStage> findByTournamentIdAndActive(UUID tournamentId, boolean active) {
        return jpaRepository.findByTournamentIdAndActive(tournamentId, active).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByTournamentIdAndStageType(UUID tournamentId, TicketSaleStage.StageType stageType) {
        var entityStageType = com.example.torneos.infrastructure.persistence.entity.TicketSaleStageEntity.StageType.valueOf(stageType.name());
        return jpaRepository.existsByTournamentIdAndStageType(tournamentId, entityStageType);
    }
}