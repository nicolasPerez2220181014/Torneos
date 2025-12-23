package com.example.torneos.infrastructure.persistence.repository;

import com.example.torneos.infrastructure.persistence.entity.TicketSaleStageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaTicketSaleStageRepository extends JpaRepository<TicketSaleStageEntity, UUID> {
    List<TicketSaleStageEntity> findByTournamentId(UUID tournamentId);
    List<TicketSaleStageEntity> findByTournamentIdAndActive(UUID tournamentId, boolean active);
    boolean existsByTournamentIdAndStageType(UUID tournamentId, TicketSaleStageEntity.StageType stageType);
    
    @Query("SELECT t FROM TicketSaleStageEntity t WHERE t.id = :id")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TicketSaleStageEntity> findByIdForUpdate(@Param("id") UUID id);
}