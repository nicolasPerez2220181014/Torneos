package com.example.torneos.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class TournamentAdmin {
    private UUID id;
    private UUID tournamentId;
    private UUID subAdminUserId;
    private LocalDateTime createdAt;

    public TournamentAdmin() {}

    public TournamentAdmin(UUID tournamentId, UUID subAdminUserId) {
        this.tournamentId = tournamentId;
        this.subAdminUserId = subAdminUserId;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getTournamentId() { return tournamentId; }
    public void setTournamentId(UUID tournamentId) { this.tournamentId = tournamentId; }

    public UUID getSubAdminUserId() { return subAdminUserId; }
    public void setSubAdminUserId(UUID subAdminUserId) { this.subAdminUserId = subAdminUserId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}