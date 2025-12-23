package com.example.torneos.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String email,
    String fullName,
    UserRole role,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public enum UserRole {
        USER, ORGANIZER, SUBADMIN
    }
}