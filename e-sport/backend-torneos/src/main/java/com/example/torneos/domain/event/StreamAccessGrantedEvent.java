package com.example.torneos.domain.event;

import java.util.UUID;

public class StreamAccessGrantedEvent extends DomainEvent {
    private final UUID accessId;
    private final UUID tournamentId;
    private final UUID userId;
    private final String accessType;
    private final UUID ticketId;

    public StreamAccessGrantedEvent(UUID accessId, UUID tournamentId, UUID userId, 
                                   String accessType, UUID ticketId) {
        super();
        this.accessId = accessId;
        this.tournamentId = tournamentId;
        this.userId = userId;
        this.accessType = accessType;
        this.ticketId = ticketId;
    }

    public UUID getAccessId() {
        return accessId;
    }

    public UUID getTournamentId() {
        return tournamentId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getAccessType() {
        return accessType;
    }

    public UUID getTicketId() {
        return ticketId;
    }
}
