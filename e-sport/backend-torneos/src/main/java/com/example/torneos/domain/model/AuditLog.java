package com.example.torneos.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuditLog {
    private UUID id;
    private EventType eventType;
    private EntityType entityType;
    private UUID entityId;
    private UUID actorUserId;
    private String metadata;
    private LocalDateTime createdAt;

    public enum EventType {
        TOURNAMENT_CREATED,
        TOURNAMENT_PUBLISHED,
        TOURNAMENT_UPDATED,
        TICKET_PURCHASED,
        TICKET_VALIDATED,
        STREAM_ACCESS_GRANTED,
        STREAM_BLOCKED,
        STREAM_UNBLOCKED,
        USER_CREATED,
        SUBADMIN_ASSIGNED
    }

    public enum EntityType {
        TOURNAMENT,
        TICKET,
        STREAM,
        USER,
        ORDER
    }

    public AuditLog() {}

    public AuditLog(EventType eventType, EntityType entityType, UUID entityId, UUID actorUserId, String metadata) {
        this.eventType = eventType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.actorUserId = actorUserId;
        this.metadata = metadata;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public EventType getEventType() { return eventType; }
    public void setEventType(EventType eventType) { this.eventType = eventType; }

    public EntityType getEntityType() { return entityType; }
    public void setEntityType(EntityType entityType) { this.entityType = entityType; }

    public UUID getEntityId() { return entityId; }
    public void setEntityId(UUID entityId) { this.entityId = entityId; }

    public UUID getActorUserId() { return actorUserId; }
    public void setActorUserId(UUID actorUserId) { this.actorUserId = actorUserId; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}