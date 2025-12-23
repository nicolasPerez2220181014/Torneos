package com.example.torneos.infrastructure.persistence.repository;

import com.example.torneos.domain.model.TicketOrder;
import com.example.torneos.domain.repository.TicketOrderRepository;
import com.example.torneos.infrastructure.persistence.mapper.TicketOrderMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TicketOrderRepositoryImpl implements TicketOrderRepository {

    private final JpaTicketOrderRepository jpaRepository;
    private final TicketOrderMapper mapper;

    public TicketOrderRepositoryImpl(JpaTicketOrderRepository jpaRepository, TicketOrderMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public TicketOrder save(TicketOrder order) {
        var entity = mapper.toEntity(order);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<TicketOrder> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<TicketOrder> findByTournamentId(UUID tournamentId) {
        return jpaRepository.findByTournamentId(tournamentId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<TicketOrder> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<TicketOrder> findByStageId(UUID stageId) {
        return jpaRepository.findByStageId(stageId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public long countByStageIdAndStatus(UUID stageId, TicketOrder.OrderStatus status) {
        var entityStatus = com.example.torneos.infrastructure.persistence.entity.TicketOrderEntity.OrderStatus.valueOf(status.name());
        return jpaRepository.countByStageIdAndStatus(stageId, entityStatus);
    }
}