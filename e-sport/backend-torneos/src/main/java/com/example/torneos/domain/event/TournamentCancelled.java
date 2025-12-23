package com.example.torneos.domain.event;

import java.time.Instant;
import java.util.UUID;

public record TournamentCancelled(
    UUID eventId,
    Instant occurredOn,
    UUID tournamentId,
    String reason
) implements DomainEvent {
    
    public TournamentCancelled(UUID tournamentId, String reason) {
        this(UUID.randomUUID(), Instant.now(), tournamentId, reason);
    }
    
    @Override
    public String getEventType() {
        return "TournamentCancelled";
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