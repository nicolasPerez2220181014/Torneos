package com.example.torneos.infrastructure.persistence.mapper;

import com.example.torneos.domain.model.TicketOrder;
import com.example.torneos.infrastructure.persistence.entity.TicketOrderEntity;
import org.springframework.stereotype.Component;

@Component
public class TicketOrderMapper {

    public TicketOrder toDomain(TicketOrderEntity entity) {
        if (entity == null) return null;
        
        TicketOrder order = new TicketOrder(
            entity.getTournamentId(),
            entity.getUserId(),
            entity.getStageId(),
            entity.getQuantity(),
            entity.getTotalAmount()
        );
        order.setId(entity.getId());
        order.setStatus(TicketOrder.OrderStatus.valueOf(entity.getStatus().name()));
        order.setCreatedAt(entity.getCreatedAt());
        return order;
    }

    public TicketOrderEntity toEntity(TicketOrder domain) {
        if (domain == null) return null;
        
        TicketOrderEntity entity = new TicketOrderEntity();
        entity.setId(domain.getId());
        entity.setTournamentId(domain.getTournamentId());
        entity.setUserId(domain.getUserId());
        entity.setStageId(domain.getStageId());
        entity.setQuantity(domain.getQuantity());
        entity.setTotalAmount(domain.getTotalAmount());
        entity.setStatus(TicketOrderEntity.OrderStatus.valueOf(domain.getStatus().name()));
        if (domain.getCreatedAt() != null) {
            entity.setCreatedAt(domain.getCreatedAt());
        }
        return entity;
    }
}