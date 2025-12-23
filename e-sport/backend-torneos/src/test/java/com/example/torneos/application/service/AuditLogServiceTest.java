package com.example.torneos.application.service;

import com.example.torneos.domain.model.AuditLog;
import com.example.torneos.domain.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    private UUID entityId;
    private UUID actorUserId;

    @BeforeEach
    void setUp() {
        entityId = UUID.randomUUID();
        actorUserId = UUID.randomUUID();
    }

    @Test
    void logEvent_Success() {
        // Given
        AuditLog.EventType eventType = AuditLog.EventType.TOURNAMENT_CREATED;
        AuditLog.EntityType entityType = AuditLog.EntityType.TOURNAMENT;
        String metadata = "Test tournament created";

        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(invocation -> {
            AuditLog auditLog = invocation.getArgument(0);
            auditLog.setId(UUID.randomUUID());
            return auditLog;
        });

        // When
        auditLogService.logEvent(eventType, entityType, entityId, actorUserId, metadata);

        // Then
        verify(auditLogRepository).save(any(AuditLog.class));
    }

    @Test
    void findAll_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        AuditLog auditLog = new AuditLog(
            AuditLog.EventType.TOURNAMENT_CREATED,
            AuditLog.EntityType.TOURNAMENT,
            entityId,
            actorUserId,
            "Test metadata"
        );
        Page<AuditLog> expectedPage = new PageImpl<>(List.of(auditLog));

        when(auditLogRepository.findAll(pageable)).thenReturn(expectedPage);

        // When
        Page<AuditLog> result = auditLogService.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(auditLog.getEventType(), result.getContent().get(0).getEventType());
        verify(auditLogRepository).findAll(pageable);
    }

    @Test
    void findByEntityId_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        AuditLog auditLog = new AuditLog(
            AuditLog.EventType.TOURNAMENT_UPDATED,
            AuditLog.EntityType.TOURNAMENT,
            entityId,
            actorUserId,
            "Test metadata"
        );
        Page<AuditLog> expectedPage = new PageImpl<>(List.of(auditLog));

        when(auditLogRepository.findByEntityId(entityId, pageable)).thenReturn(expectedPage);

        // When
        Page<AuditLog> result = auditLogService.findByEntityId(entityId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(entityId, result.getContent().get(0).getEntityId());
        verify(auditLogRepository).findByEntityId(entityId, pageable);
    }

    @Test
    void findByActorUserId_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        AuditLog auditLog = new AuditLog(
            AuditLog.EventType.TICKET_PURCHASED,
            AuditLog.EntityType.ORDER,
            entityId,
            actorUserId,
            "Test metadata"
        );
        Page<AuditLog> expectedPage = new PageImpl<>(List.of(auditLog));

        when(auditLogRepository.findByActorUserId(actorUserId, pageable)).thenReturn(expectedPage);

        // When
        Page<AuditLog> result = auditLogService.findByActorUserId(actorUserId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(actorUserId, result.getContent().get(0).getActorUserId());
        verify(auditLogRepository).findByActorUserId(actorUserId, pageable);
    }

    @Test
    void findByEventType_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        AuditLog.EventType eventType = AuditLog.EventType.STREAM_BLOCKED;
        AuditLog auditLog = new AuditLog(
            eventType,
            AuditLog.EntityType.STREAM,
            entityId,
            actorUserId,
            "Stream blocked for inappropriate content"
        );
        Page<AuditLog> expectedPage = new PageImpl<>(List.of(auditLog));

        when(auditLogRepository.findByEventType(eventType, pageable)).thenReturn(expectedPage);

        // When
        Page<AuditLog> result = auditLogService.findByEventType(eventType, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(eventType, result.getContent().get(0).getEventType());
        verify(auditLogRepository).findByEventType(eventType, pageable);
    }

    @Test
    void findByEntityType_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        AuditLog.EntityType entityType = AuditLog.EntityType.TICKET;
        AuditLog auditLog = new AuditLog(
            AuditLog.EventType.TICKET_VALIDATED,
            entityType,
            entityId,
            actorUserId,
            "Ticket validated successfully"
        );
        Page<AuditLog> expectedPage = new PageImpl<>(List.of(auditLog));

        when(auditLogRepository.findByEntityType(entityType, pageable)).thenReturn(expectedPage);

        // When
        Page<AuditLog> result = auditLogService.findByEntityType(entityType, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(entityType, result.getContent().get(0).getEntityType());
        verify(auditLogRepository).findByEntityType(entityType, pageable);
    }
}