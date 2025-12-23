package com.example.torneos.domain.event;

import java.time.Instant;
import java.util.UUID;

public record TournamentPublished(
    UUID eventId,
    Instant occurredOn,
    UUID tournamentId,
    String tournamentName,
    UUID organizerId
) implements DomainEvent {
    
    public TournamentPublished(UUID tournamentId, String tournamentName, UUID organizerId) {
        this(UUID.randomUUID(), Instant.now(), tournamentId, tournamentName, organizerId);
    }
    
    @Override
    public String getEventType() {
        return "TournamentPublished";
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