package com.example.torneos.application.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para la creación de etapas de venta de tickets.
 * Representa los diferentes períodos de venta con precios y capacidades específicas.
 */
public record CreateTicketSaleStageRequest(
    // Tipo de etapa de venta (madrugador, regular, último minuto)
    @NotNull(message = "El tipo de etapa es obligatorio")
    StageType stageType,
    
    // Precio del ticket en esta etapa
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio debe ser mayor o igual a 0")
    @DecimalMax(value = "999999.99", message = "El precio no puede exceder $999,999.99")
    BigDecimal price,
    
    // Número máximo de tickets disponibles en esta etapa
    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser mayor a 0")
    @Max(value = 50000, message = "La capacidad no puede exceder 50,000")
    Integer capacity,
    
    // Fecha y hora de inicio de la venta
    @NotNull(message = "La fecha de inicio es obligatoria")
    @Future(message = "La fecha de inicio debe ser futura")
    LocalDateTime startDateTime,
    
    // Fecha y hora de finalización de la venta
    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser futura")
    LocalDateTime endDateTime
) {
    
    /**
     * Valida que la fecha de finalización sea posterior a la fecha de inicio.
     * Esta validación se ejecuta automáticamente durante la validación del objeto.
     * 
     * @return true si las fechas son válidas, false en caso contrario
     */
    @AssertTrue(message = "La fecha de fin debe ser posterior a la fecha de inicio")
    public boolean isEndDateAfterStartDate() {
        if (endDateTime == null || startDateTime == null) {
            return false;
        }
        return endDateTime.isAfter(startDateTime);
    }
    
    /**
     * Enumera los tipos de etapas de venta disponibles.
     * Cada tipo representa un período específico con características particulares.
     */
    public enum StageType {
        /** Venta anticipada con descuentos especiales */
        EARLY_BIRD,
        
        /** Venta regular a precio estándar */
        REGULAR,
        
        /** Venta de último minuto, generalmente a precio premium */
        LAST_MINUTE
    }
}