package com.example.torneos.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Ticket {
    private UUID id;
    private UUID orderId;
    private UUID tournamentId;
    private UUID userId;
    private String accessCode;
    private TicketStatus status;
    private LocalDateTime usedAt;
    private LocalDateTime createdAt;

    public enum TicketStatus {
        ISSUED, USED, CANCELLED
    }

    public Ticket() {}

    public Ticket(UUID orderId, UUID tournamentId, UUID userId, String accessCode) {
        this.orderId = orderId;
        this.tournamentId = tournamentId;
        this.userId = userId;
        this.accessCode = accessCode;
        this.status = TicketStatus.ISSUED;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }

    public UUID getTournamentId() { return tournamentId; }
    public void setTournamentId(UUID tournamentId) { this.tournamentId = tournamentId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getAccessCode() { return accessCode; }
    public void setAccessCode(String accessCode) { this.accessCode = accessCode; }

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }

    public LocalDateTime getUsedAt() { return usedAt; }
    public void setUsedAt(LocalDateTime usedAt) { this.usedAt = usedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}