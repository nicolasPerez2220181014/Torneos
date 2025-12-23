package com.example.torneos.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateTicketOrderRequest(
    @NotNull(message = "El ID de la etapa es obligatorio")
    UUID stageId,
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    @Max(value = 50, message = "No se pueden comprar más de 50 tickets por orden")
    Integer quantity
) {}