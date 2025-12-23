package com.example.torneos.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class StreamAccess {
    private UUID id;
    private UUID tournamentId;
    private UUID userId;
    private AccessType accessType;
    private UUID ticketId;
    private LocalDateTime createdAt;

    public enum AccessType {
        FREE, PAID
    }

    public StreamAccess() {}

    public StreamAccess(UUID tournamentId, UUID userId, AccessType accessType, UUID ticketId) {
        this.tournamentId = tournamentId;
        this.userId = userId;
        this.accessType = accessType;
        this.ticketId = ticketId;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getTournamentId() { return tournamentId; }
    public void setTournamentId(UUID tournamentId) { this.tournamentId = tournamentId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public AccessType getAccessType() { return accessType; }
    public void setAccessType(AccessType accessType) { this.accessType = accessType; }

    public UUID getTicketId() { return ticketId; }
    public void setTicketId(UUID ticketId) { this.ticketId = ticketId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}