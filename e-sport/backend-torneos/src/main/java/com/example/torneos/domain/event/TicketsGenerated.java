package com.example.torneos.domain.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TicketsGenerated(
    UUID eventId,
    Instant occurredOn,
    UUID orderId,
    UUID tournamentId,
    UUID userId,
    List<String> accessCodes
) implements DomainEvent {
    
    public TicketsGenerated(UUID orderId, UUID tournamentId, UUID userId, List<String> accessCodes) {
        this(UUID.randomUUID(), Instant.now(), orderId, tournamentId, userId, accessCodes);
    }
    
    @Override
    public String getEventType() {
        return "TicketsGenerated";
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