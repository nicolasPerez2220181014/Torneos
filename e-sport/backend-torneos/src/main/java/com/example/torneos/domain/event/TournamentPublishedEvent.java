package com.example.torneos.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class TournamentPublishedEvent extends DomainEvent {
    private final UUID tournamentId;
    private final UUID organizerId;
    private final String tournamentName;
    private final boolean isPaid;
    private final LocalDateTime startDateTime;

    public TournamentPublishedEvent(UUID tournamentId, UUID organizerId, String tournamentName, 
                                   boolean isPaid, LocalDateTime startDateTime) {
        super();
        this.tournamentId = tournamentId;
        this.organizerId = organizerId;
        this.tournamentName = tournamentName;
        this.isPaid = isPaid;
        this.startDateTime = startDateTime;
    }

    public UUID getTournamentId() {
        return tournamentId;
    }

    public UUID getOrganizerId() {
        return organizerId;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }
}
