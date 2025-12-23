package com.example.torneos.application.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateTournamentRequest(
    @NotNull(message = "La categoría es obligatoria")
    UUID categoryId,
    
    @NotNull(message = "El tipo de juego es obligatorio")
    UUID gameTypeId,
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    String name,
    
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    String description,
    
    @NotNull(message = "Debe especificar si es torneo pago")
    Boolean isPaid,
    
    @Min(value = 1, message = "La capacidad gratuita debe ser mayor a 0")
    @Max(value = 10000, message = "La capacidad gratuita no puede exceder 10000")
    Integer maxFreeCapacity,
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    LocalDateTime startDateTime,
    
    @NotNull(message = "La fecha de fin es obligatoria")
    LocalDateTime endDateTime
) {
    @AssertTrue(message = "La fecha de fin debe ser posterior a la fecha de inicio")
    public boolean isEndDateAfterStartDate() {
        return endDateTime != null && startDateTime != null && 
               endDateTime.isAfter(startDateTime);
    }
}