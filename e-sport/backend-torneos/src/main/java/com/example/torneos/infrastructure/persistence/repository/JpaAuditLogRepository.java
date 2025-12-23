package com.example.torneos.infrastructure.persistence.repository;

import com.example.torneos.infrastructure.persistence.entity.AuditLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaAuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {
    Page<AuditLogEntity> findByEntityId(UUID entityId, Pageable pageable);
    Page<AuditLogEntity> findByActorUserId(UUID actorUserId, Pageable pageable);
    Page<AuditLogEntity> findByEventType(AuditLogEntity.EventType eventType, Pageable pageable);
    Page<AuditLogEntity> findByEntityType(AuditLogEntity.EntityType entityType, Pageable pageable);
}