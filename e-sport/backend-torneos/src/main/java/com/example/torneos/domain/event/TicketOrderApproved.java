package com.example.torneos.domain.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TicketOrderApproved(
    UUID eventId,
    Instant occurredOn,
    UUID orderId,
    UUID tournamentId,
    UUID userId,
    int quantity,
    BigDecimal totalAmount
) implements DomainEvent {
    
    public TicketOrderApproved(UUID orderId, UUID tournamentId, UUID userId, int quantity, BigDecimal totalAmount) {
        this(UUID.randomUUID(), Instant.now(), orderId, tournamentId, userId, quantity, totalAmount);
    }
    
    @Override
    public String getEventType() {
        return "TicketOrderApproved";
    }
    
    @Override
    public UUID getEventId() {
        return eventId;
    }
    
    @Override
    public Instant getOccurredOn() {
        return occurredOn;
    }
}