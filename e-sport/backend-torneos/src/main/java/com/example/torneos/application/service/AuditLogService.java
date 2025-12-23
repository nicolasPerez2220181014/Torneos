package com.example.torneos.application.service;

import com.example.torneos.domain.model.AuditLog;
import com.example.torneos.domain.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logEvent(AuditLog.EventType eventType, AuditLog.EntityType entityType, 
                        UUID entityId, UUID actorUserId, String metadata) {
        AuditLog auditLog = new AuditLog(eventType, entityType, entityId, actorUserId, metadata);
        auditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findAll(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findByEntityId(UUID entityId, Pageable pageable) {
        return auditLogRepository.findByEntityId(entityId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findByActorUserId(UUID actorUserId, Pageable pageable) {
        return auditLogRepository.findByActorUserId(actorUserId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findByEventType(AuditLog.EventType eventType, Pageable pageable) {
        return auditLogRepository.findByEventType(eventType, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findByEntityType(AuditLog.EntityType entityType, Pageable pageable) {
        return auditLogRepository.findByEntityType(entityType, pageable);
    }
}