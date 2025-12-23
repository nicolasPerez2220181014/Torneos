package com.example.torneos.infrastructure.validation;

import com.example.torneos.application.dto.request.CreateTournamentRequest;
import com.example.torneos.application.dto.request.UpdateTournamentRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TournamentDateValidator implements ConstraintValidator<ValidTournamentDates, Object> {
    
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;
        
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        
        if (value instanceof CreateTournamentRequest request) {
            startDateTime = request.startDateTime();
            endDateTime = request.endDateTime();
        } else if (value instanceof UpdateTournamentRequest request) {
            startDateTime = request.startDateTime();
            endDateTime = request.endDateTime();
        }
        
        if (startDateTime == null || endDateTime == null) return true;
        
        // Minimum duration: 1 hour
        if (ChronoUnit.HOURS.between(startDateTime, endDateTime) < 1) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("El torneo debe durar al menos 1 hora")
                   .addConstraintViolation();
            return false;
        }
        
        // Maximum duration: 30 days
        if (ChronoUnit.DAYS.between(startDateTime, endDateTime) > 30) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("El torneo no puede durar más de 30 días")
                   .addConstraintViolation();
            return false;
        }
        
        return true;
    }
}