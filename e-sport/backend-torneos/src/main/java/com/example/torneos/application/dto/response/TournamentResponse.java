package com.example.torneos.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record TournamentResponse(
    UUID id,
    UUID organizerId,
    UUID categoryId,
    UUID gameTypeId,
    String name,
    String description,
    boolean isPaid,
    Integer maxFreeCapacity,
    LocalDateTime startDateTime,
    LocalDateTime endDateTime,
    TournamentStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public enum TournamentStatus {
        DRAFT, PUBLISHED, FINISHED, CANCELLED
    }
}