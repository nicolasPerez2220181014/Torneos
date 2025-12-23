package com.example.torneos.domain.repository;

import com.example.torneos.domain.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AuditLogRepository {
    AuditLog save(AuditLog auditLog);
    Page<AuditLog> findAll(Pageable pageable);
    Page<AuditLog> findByEntityId(UUID entityId, Pageable pageable);
    Page<AuditLog> findByActorUserId(UUID actorUserId, Pageable pageable);
    Page<AuditLog> findByEventType(AuditLog.EventType eventType, Pageable pageable);
    Page<AuditLog> findByEntityType(AuditLog.EntityType entityType, Pageable pageable);
}