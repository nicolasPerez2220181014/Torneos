package com.example.torneos.infrastructure.controller;

import com.example.torneos.application.dto.request.LoginRequestDto;
import com.example.torneos.application.service.AuthenticationService;
import com.example.torneos.application.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Authentication", description = "API para autenticación y tokens")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve tokens")
    public ResponseEntity<AuthenticationService.AuthResponse> login(
            @RequestBody LoginRequestDto request) {
        
        AuthenticationService.AuthResponse response = authenticationService.login(request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar token", description = "Renueva el access token usando el refresh token")
    public ResponseEntity<RefreshTokenService.AuthResponse> refreshToken(
            @RequestBody RefreshTokenRequest request) {
        
        RefreshTokenService.AuthResponse response = authenticationService.refreshToken(request.refreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Revoca todos los refresh tokens del usuario")
    public ResponseEntity<Void> logout(@RequestHeader("X-USER-EMAIL") String userEmail) {
        authenticationService.logout(userEmail);
        return ResponseEntity.ok().build();
    }

    public record RefreshTokenRequest(String refreshToken) {}
}