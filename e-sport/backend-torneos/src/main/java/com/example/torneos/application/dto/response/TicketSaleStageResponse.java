package com.example.torneos.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TicketSaleStageResponse(
    UUID id,
    UUID tournamentId,
    StageType stageType,
    BigDecimal price,
    Integer capacity,
    LocalDateTime startDateTime,
    LocalDateTime endDateTime,
    boolean active
) {
    public enum StageType {
        EARLY_BIRD, REGULAR, LAST_MINUTE
    }
}