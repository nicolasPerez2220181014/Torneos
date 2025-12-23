package com.example.torneos.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenRequestDto {
    @NotBlank(message = "Refresh token es obligatorio")
    private String refreshToken;

    public RefreshTokenRequestDto() {}

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}