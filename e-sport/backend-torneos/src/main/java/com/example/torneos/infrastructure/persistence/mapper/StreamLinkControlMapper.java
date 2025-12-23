package com.example.torneos.infrastructure.persistence.mapper;

import com.example.torneos.domain.model.StreamLinkControl;
import com.example.torneos.infrastructure.persistence.entity.StreamLinkControlEntity;
import org.springframework.stereotype.Component;

@Component
public class StreamLinkControlMapper {

    public StreamLinkControl toDomain(StreamLinkControlEntity entity) {
        if (entity == null) return null;
        
        StreamLinkControl domain = new StreamLinkControl();
        domain.setId(entity.getId());
        domain.setTournamentId(entity.getTournamentId());
        domain.setStreamUrl(entity.getStreamUrl());
        domain.setBlocked(entity.isBlocked());
        domain.setBlockReason(entity.getBlockReason());
        domain.setBlockedAt(entity.getBlockedAt());
        domain.setCreatedAt(entity.getCreatedAt());
        return domain;
    }

    public StreamLinkControlEntity toEntity(StreamLinkControl domain) {
        if (domain == null) return null;
        
        StreamLinkControlEntity entity = new StreamLinkControlEntity();
        entity.setId(domain.getId());
        entity.setTournamentId(domain.getTournamentId());
        entity.setStreamUrl(domain.getStreamUrl());
        entity.setBlocked(domain.isBlocked());
        entity.setBlockReason(domain.getBlockReason());
        entity.setBlockedAt(domain.getBlockedAt());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }
}