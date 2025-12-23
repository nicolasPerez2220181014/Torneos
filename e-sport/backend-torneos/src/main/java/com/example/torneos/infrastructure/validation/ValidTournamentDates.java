package com.example.torneos.infrastructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TournamentDateValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTournamentDates {
    String message() default "Las fechas del torneo no son válidas";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}