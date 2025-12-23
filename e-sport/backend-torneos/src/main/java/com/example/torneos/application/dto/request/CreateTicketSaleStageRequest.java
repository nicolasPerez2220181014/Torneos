package com.example.torneos.application.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateTicketSaleStageRequest(
    @NotNull(message = "El tipo de etapa es obligatorio")
    StageType stageType,
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio debe ser mayor o igual a 0")
    @DecimalMax(value = "999999.99", message = "El precio no puede exceder $999,999.99")
    BigDecimal price,
    
    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    @Max(value = 50000, message = "La capacidad no puede exceder 50,000")
    Integer capacity,
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    @Future(message = "La fecha de inicio debe ser futura")
    LocalDateTime startDateTime,
    
    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser futura")
    LocalDateTime endDateTime
) {
    @AssertTrue(message = "La fecha de fin debe ser posterior a la fecha de inicio")
    public boolean isEndDateAfterStartDate() {
        return endDateTime != null && startDateTime != null && 
               endDateTime.isAfter(startDateTime);
    }
    
    public enum StageType {
        EARLY_BIRD, REGULAR, LAST_MINUTE
    }
}