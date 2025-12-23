package com.example.torneos.application.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record CreateTournamentRequest(
    String categoryId,
    String gameTypeId,
    String name,
    String description,
    Boolean isPaid,
    Integer maxFreeCapacity,
    LocalDateTime startDateTime,
    LocalDateTime endDateTime
) {}