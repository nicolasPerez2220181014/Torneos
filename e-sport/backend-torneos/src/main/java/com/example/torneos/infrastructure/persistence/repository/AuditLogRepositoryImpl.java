package com.example.torneos.infrastructure.persistence.repository;

import com.example.torneos.domain.model.AuditLog;
import com.example.torneos.domain.repository.AuditLogRepository;
import com.example.torneos.infrastructure.persistence.mapper.AuditLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class AuditLogRepositoryImpl implements AuditLogRepository {

    @Autowired
    private JpaAuditLogRepository jpaRepository;

    @Autowired
    private AuditLogMapper mapper;

    @Override
    public AuditLog save(AuditLog auditLog) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(auditLog)));
    }

    @Override
    public Page<AuditLog> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public Page<AuditLog> findByEntityId(UUID entityId, Pageable pageable) {
        return jpaRepository.findByEntityId(entityId, pageable).map(mapper::toDomain);
    }

    @Override
    public Page<AuditLog> findByActorUserId(UUID actorUserId, Pageable pageable) {
        return jpaRepository.findByActorUserId(actorUserId, pageable).map(mapper::toDomain);
    }

    @Override
    public Page<AuditLog> findByEventType(AuditLog.EventType eventType, Pageable pageable) {
        return jpaRepository.findByEventType(
            com.example.torneos.infrastructure.persistence.entity.AuditLogEntity.EventType.valueOf(eventType.name()),
            pageable
        ).map(mapper::toDomain);
    }

    @Override
    public Page<AuditLog> findByEntityType(AuditLog.EntityType entityType, Pageable pageable) {
        return jpaRepository.findByEntityType(
            com.example.torneos.infrastructure.persistence.entity.AuditLogEntity.EntityType.valueOf(entityType.name()),
            pageable
        ).map(mapper::toDomain);
    }
}