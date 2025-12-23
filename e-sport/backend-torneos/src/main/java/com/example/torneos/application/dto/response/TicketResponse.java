package com.example.torneos.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record TicketResponse(
    UUID id,
    UUID orderId,
    UUID tournamentId,
    UUID userId,
    String accessCode,
    TicketStatus status,
    LocalDateTime usedAt,
    LocalDateTime createdAt
) {
    public enum TicketStatus {
        ISSUED, USED, CANCELLED
    }
}