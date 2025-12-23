package com.example.torneos.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    String email,
    
    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    String fullName,
    
    @NotNull(message = "El rol es obligatorio")
    UserRole role
) {
    public enum UserRole {
        USER, ORGANIZER, SUBADMIN
    }
}