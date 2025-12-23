package com.example.torneos.infrastructure.persistence.mapper;

import com.example.torneos.domain.model.Ticket;
import com.example.torneos.infrastructure.persistence.entity.TicketEntity;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public Ticket toDomain(TicketEntity entity) {
        if (entity == null) return null;
        
        Ticket ticket = new Ticket(
            entity.getOrderId(),
            entity.getTournamentId(),
            entity.getUserId(),
            entity.getAccessCode()
        );
        ticket.setId(entity.getId());
        ticket.setStatus(Ticket.TicketStatus.valueOf(entity.getStatus().name()));
        ticket.setUsedAt(entity.getUsedAt());
        ticket.setCreatedAt(entity.getCreatedAt());
        return ticket;
    }

    public TicketEntity toEntity(Ticket domain) {
        if (domain == null) return null;
        
        TicketEntity entity = new TicketEntity();
        entity.setId(domain.getId());
        entity.setOrderId(domain.getOrderId());
        entity.setTournamentId(domain.getTournamentId());
        entity.setUserId(domain.getUserId());
        entity.setAccessCode(domain.getAccessCode());
        entity.setStatus(TicketEntity.TicketStatus.valueOf(domain.getStatus().name()));
        entity.setUsedAt(domain.getUsedAt());
        if (domain.getCreatedAt() != null) {
            entity.setCreatedAt(domain.getCreatedAt());
        }
        return entity;
    }
}