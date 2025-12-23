package com.example.torneos.infrastructure.persistence.mapper;

import com.example.torneos.domain.model.TicketSaleStage;
import com.example.torneos.infrastructure.persistence.entity.TicketSaleStageEntity;
import org.springframework.stereotype.Component;

@Component
public class TicketSaleStageMapper {

    public TicketSaleStage toDomain(TicketSaleStageEntity entity) {
        if (entity == null) return null;
        
        TicketSaleStage stage = new TicketSaleStage(
            entity.getTournamentId(),
            TicketSaleStage.StageType.valueOf(entity.getStageType().name()),
            entity.getPrice(),
            entity.getCapacity(),
            entity.getStartDateTime(),
            entity.getEndDateTime()
        );
        stage.setId(entity.getId());
        stage.setActive(entity.isActive());
        return stage;
    }

    public TicketSaleStageEntity toEntity(TicketSaleStage domain) {
        if (domain == null) return null;
        
        TicketSaleStageEntity entity = new TicketSaleStageEntity();
        entity.setId(domain.getId());
        entity.setTournamentId(domain.getTournamentId());
        entity.setStageType(TicketSaleStageEntity.StageType.valueOf(domain.getStageType().name()));
        entity.setPrice(domain.getPrice());
        entity.setCapacity(domain.getCapacity());
        entity.setStartDateTime(domain.getStartDateTime());
        entity.setEndDateTime(domain.getEndDateTime());
        entity.setActive(domain.isActive());
        return entity;
    }
}