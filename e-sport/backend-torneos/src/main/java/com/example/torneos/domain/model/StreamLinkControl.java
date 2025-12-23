package com.example.torneos.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class StreamLinkControl {
    private UUID id;
    private UUID tournamentId;
    private String streamUrl;
    private boolean blocked;
    private String blockReason;
    private LocalDateTime blockedAt;
    private LocalDateTime createdAt;

    public StreamLinkControl() {}

    public StreamLinkControl(UUID tournamentId, String streamUrl) {
        this.tournamentId = tournamentId;
        this.streamUrl = streamUrl;
        this.blocked = false;
        this.createdAt = LocalDateTime.now();
    }

    public void block(String reason) {
        this.blocked = true;
        this.blockReason = reason;
        this.blockedAt = LocalDateTime.now();
    }

    public void unblock() {
        this.blocked = false;
        this.blockReason = null;
        this.blockedAt = null;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getTournamentId() { return tournamentId; }
    public void setTournamentId(UUID tournamentId) { this.tournamentId = tournamentId; }

    public String getStreamUrl() { return streamUrl; }
    public void setStreamUrl(String streamUrl) { this.streamUrl = streamUrl; }

    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }

    public String getBlockReason() { return blockReason; }
    public void setBlockReason(String blockReason) { this.blockReason = blockReason; }

    public LocalDateTime getBlockedAt() { return blockedAt; }
    public void setBlockedAt(LocalDateTime blockedAt) { this.blockedAt = blockedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}