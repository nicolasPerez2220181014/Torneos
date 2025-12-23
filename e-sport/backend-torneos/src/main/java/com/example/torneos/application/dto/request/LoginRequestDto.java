package com.example.torneos.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequestDto {
    @NotBlank(message = "Email es obligatorio")
    @Email(message = "Email debe tener formato válido")
    private String email;

    public LoginRequestDto() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}