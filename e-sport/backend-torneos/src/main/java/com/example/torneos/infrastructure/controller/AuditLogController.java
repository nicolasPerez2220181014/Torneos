package com.example.torneos.infrastructure.controller;

import com.example.torneos.application.dto.response.AuditLogResponseDto;
import com.example.torneos.application.service.AuditLogService;
import com.example.torneos.domain.model.AuditLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/audit-logs")
@Tag(name = "Audit Logs", description = "Consulta de logs de auditoría del sistema")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los logs de auditoría", 
               description = "Obtiene todos los logs de auditoría con paginación")
    public ResponseEntity<Page<AuditLogResponseDto>> findAll(Pageable pageable) {
        Page<AuditLog> auditLogs = auditLogService.findAll(pageable);
        Page<AuditLogResponseDto> response = auditLogs.map(this::mapToResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/entity/{entityId}")
    @Operation(summary = "Listar logs por entidad", 
               description = "Obtiene logs de auditoría filtrados por ID de entidad")
    public ResponseEntity<Page<AuditLogResponseDto>> findByEntityId(
            @Parameter(description = "ID de la entidad") @PathVariable UUID entityId,
            Pageable pageable) {
        
        Page<AuditLog> auditLogs = auditLogService.findByEntityId(entityId, pageable);
        Page<AuditLogResponseDto> response = auditLogs.map(this::mapToResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/actor/{actorUserId}")
    @Operation(summary = "Listar logs por actor", 
               description = "Obtiene logs de auditoría filtrados por usuario que ejecutó la acción")
    public ResponseEntity<Page<AuditLogResponseDto>> findByActorUserId(
            @Parameter(description = "ID del usuario actor") @PathVariable UUID actorUserId,
            Pageable pageable) {
        
        Page<AuditLog> auditLogs = auditLogService.findByActorUserId(actorUserId, pageable);
        Page<AuditLogResponseDto> response = auditLogs.map(this::mapToResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/event-type/{eventType}")
    @Operation(summary = "Listar logs por tipo de evento", 
               description = "Obtiene logs de auditoría filtrados por tipo de evento")
    public ResponseEntity<Page<AuditLogResponseDto>> findByEventType(
            @Parameter(description = "Tipo de evento") @PathVariable String eventType,
            Pageable pageable) {
        
        try {
            AuditLog.EventType eventTypeEnum = AuditLog.EventType.valueOf(eventType.toUpperCase());
            Page<AuditLog> auditLogs = auditLogService.findByEventType(eventTypeEnum, pageable);
            Page<AuditLogResponseDto> response = auditLogs.map(this::mapToResponse);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/entity-type/{entityType}")
    @Operation(summary = "Listar logs por tipo de entidad", 
               description = "Obtiene logs de auditoría filtrados por tipo de entidad")
    public ResponseEntity<Page<AuditLogResponseDto>> findByEntityType(
            @Parameter(description = "Tipo de entidad") @PathVariable String entityType,
            Pageable pageable) {
        
        try {
            AuditLog.EntityType entityTypeEnum = AuditLog.EntityType.valueOf(entityType.toUpperCase());
            Page<AuditLog> auditLogs = auditLogService.findByEntityType(entityTypeEnum, pageable);
            Page<AuditLogResponseDto> response = auditLogs.map(this::mapToResponse);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private AuditLogResponseDto mapToResponse(AuditLog auditLog) {
        AuditLogResponseDto response = new AuditLogResponseDto();
        response.setId(auditLog.getId());
        response.setEventType(auditLog.getEventType().name());
        response.setEntityType(auditLog.getEntityType().name());
        response.setEntityId(auditLog.getEntityId());
        response.setActorUserId(auditLog.getActorUserId());
        response.setMetadata(auditLog.getMetadata());
        response.setCreatedAt(auditLog.getCreatedAt());
        return response;
    }
}