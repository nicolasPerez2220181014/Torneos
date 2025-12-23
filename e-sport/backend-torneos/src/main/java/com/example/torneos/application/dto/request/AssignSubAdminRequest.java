package com.example.torneos.application.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AssignSubAdminRequest(
    @NotNull(message = "El ID del usuario subadministrador es obligatorio")
    UUID subAdminUserId
) {}