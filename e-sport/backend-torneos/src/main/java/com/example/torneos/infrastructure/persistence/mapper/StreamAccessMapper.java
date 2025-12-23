package com.example.torneos.infrastructure.persistence.mapper;

import com.example.torneos.domain.model.StreamAccess;
import com.example.torneos.infrastructure.persistence.entity.StreamAccessEntity;
import org.springframework.stereotype.Component;

@Component
public class StreamAccessMapper {

    public StreamAccess toDomain(StreamAccessEntity entity) {
        if (entity == null) return null;
        
        StreamAccess domain = new StreamAccess();
        domain.setId(entity.getId());
        domain.setTournamentId(entity.getTournamentId());
        domain.setUserId(entity.getUserId());
        domain.setAccessType(StreamAccess.AccessType.valueOf(entity.getAccessType().name()));
        domain.setTicketId(entity.getTicketId());
        domain.setCreatedAt(entity.getCreatedAt());
        return domain;
    }

    public StreamAccessEntity toEntity(StreamAccess domain) {
        if (domain == null) return null;
        
        StreamAccessEntity entity = new StreamAccessEntity();
        entity.setId(domain.getId());
        entity.setTournamentId(domain.getTournamentId());
        entity.setUserId(domain.getUserId());
        entity.setAccessType(StreamAccessEntity.AccessType.valueOf(domain.getAccessType().name()));
        entity.setTicketId(domain.getTicketId());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }
}