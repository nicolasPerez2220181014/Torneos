package com.example.torneos.infrastructure.persistence.mapper;

import com.example.torneos.domain.model.AuditLog;
import com.example.torneos.infrastructure.persistence.entity.AuditLogEntity;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {

    public AuditLog toDomain(AuditLogEntity entity) {
        if (entity == null) return null;
        
        AuditLog domain = new AuditLog();
        domain.setId(entity.getId());
        domain.setEventType(AuditLog.EventType.valueOf(entity.getEventType().name()));
        domain.setEntityType(AuditLog.EntityType.valueOf(entity.getEntityType().name()));
        domain.setEntityId(entity.getEntityId());
        domain.setActorUserId(entity.getActorUserId());
        domain.setMetadata(entity.getMetadata());
        domain.setCreatedAt(entity.getCreatedAt());
        return domain;
    }

    public AuditLogEntity toEntity(AuditLog domain) {
        if (domain == null) return null;
        
        AuditLogEntity entity = new AuditLogEntity();
        entity.setId(domain.getId());
        entity.setEventType(AuditLogEntity.EventType.valueOf(domain.getEventType().name()));
        entity.setEntityType(AuditLogEntity.EntityType.valueOf(domain.getEntityType().name()));
        entity.setEntityId(domain.getEntityId());
        entity.setActorUserId(domain.getActorUserId());
        entity.setMetadata(domain.getMetadata());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }
}