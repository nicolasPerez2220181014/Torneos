package com.example.torneos.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record TournamentAdminResponse(
    UUID id,
    UUID tournamentId,
    UUID subAdminUserId,
    LocalDateTime createdAt
) {}