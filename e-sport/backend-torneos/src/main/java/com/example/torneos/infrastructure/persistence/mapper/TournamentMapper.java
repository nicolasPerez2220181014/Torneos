package com.example.torneos.infrastructure.persistence.mapper;

import com.example.torneos.domain.model.Tournament;
import com.example.torneos.infrastructure.persistence.entity.TournamentEntity;
import org.springframework.stereotype.Component;

@Component
public class TournamentMapper {

    public Tournament toDomain(TournamentEntity entity) {
        if (entity == null) return null;
        
        Tournament tournament = new Tournament(
            entity.getOrganizerId(),
            entity.getCategoryId(),
            entity.getGameTypeId(),
            entity.getName(),
            entity.getDescription(),
            entity.isPaid(),
            entity.getMaxFreeCapacity(),
            entity.getStartDateTime(),
            entity.getEndDateTime()
        );
        tournament.setId(entity.getId());
        tournament.setStatus(Tournament.TournamentStatus.valueOf(entity.getStatus().name()));
        tournament.setCreatedAt(entity.getCreatedAt());
        tournament.setUpdatedAt(entity.getUpdatedAt());
        return tournament;
    }

    public TournamentEntity toEntity(Tournament domain) {
        if (domain == null) return null;
        
        TournamentEntity entity = new TournamentEntity();
        // Solo establecer ID si ya existe (para updates)
        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }
        entity.setOrganizerId(domain.getOrganizerId());
        entity.setCategoryId(domain.getCategoryId());
        entity.setGameTypeId(domain.getGameTypeId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setPaid(domain.isPaid());
        entity.setMaxFreeCapacity(domain.getMaxFreeCapacity());
        entity.setStartDateTime(domain.getStartDateTime());
        entity.setEndDateTime(domain.getEndDateTime());
        entity.setStatus(TournamentEntity.TournamentStatus.valueOf(domain.getStatus().name()));
        if (domain.getCreatedAt() != null) {
            entity.setCreatedAt(domain.getCreatedAt());
        }
        if (domain.getUpdatedAt() != null) {
            entity.setUpdatedAt(domain.getUpdatedAt());
        }
        return entity;
    }
}