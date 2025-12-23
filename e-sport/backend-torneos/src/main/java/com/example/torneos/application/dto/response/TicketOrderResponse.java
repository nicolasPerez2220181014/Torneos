package com.example.torneos.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TicketOrderResponse(
    UUID id,
    UUID tournamentId,
    UUID userId,
    UUID stageId,
    Integer quantity,
    BigDecimal totalAmount,
    OrderStatus status,
    LocalDateTime createdAt
) {
    public enum OrderStatus {
        PENDING, APPROVED, REJECTED
    }
}