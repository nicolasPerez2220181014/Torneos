package com.example.torneos.infrastructure.controller;

import com.example.torneos.application.dto.request.StreamAccessRequestDto;
import com.example.torneos.application.dto.response.StreamAccessResponseDto;
import com.example.torneos.application.service.StreamAccessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tournaments/{tournamentId}/stream")
@Tag(name = "Stream Access", description = "Gestión de acceso a streams de torneos")
public class StreamAccessController {

    private final StreamAccessService streamAccessService;

    public StreamAccessController(StreamAccessService streamAccessService) {
        this.streamAccessService = streamAccessService;
    }

    @PostMapping("/access")
    @Operation(summary = "Solicitar acceso al stream", 
               description = "Permite a un usuario solicitar acceso FREE (máximo 1) o PAID (con ticket) al stream")
    public ResponseEntity<StreamAccessResponseDto> requestAccess(
            @Parameter(description = "ID del torneo") @PathVariable UUID tournamentId,
            @Parameter(description = "ID del usuario") @RequestHeader("X-USER-ID") UUID userId,
            @Valid @RequestBody StreamAccessRequestDto request) {
        
        StreamAccessResponseDto response = streamAccessService.requestAccess(tournamentId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/access")
    @Operation(summary = "Obtener acceso del usuario", 
               description = "Obtiene el acceso del usuario autenticado al stream del torneo")
    public ResponseEntity<StreamAccessResponseDto> getUserAccess(
            @Parameter(description = "ID del torneo") @PathVariable UUID tournamentId,
            @Parameter(description = "ID del usuario") @RequestHeader("X-USER-ID") UUID userId) {
        
        StreamAccessResponseDto response = streamAccessService.findByTournamentIdAndUserId(tournamentId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/access/all")
    @Operation(summary = "Listar todos los accesos del torneo", 
               description = "Lista todos los accesos al stream del torneo (solo para organizadores)")
    public ResponseEntity<List<StreamAccessResponseDto>> getAllAccess(
            @Parameter(description = "ID del torneo") @PathVariable UUID tournamentId,
            @Parameter(description = "Rol del usuario") @RequestHeader("X-ROLE") String role) {
        
        if (!"ORGANIZER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<StreamAccessResponseDto> response = streamAccessService.findByTournamentId(tournamentId);
        return ResponseEntity.ok(response);
    }
}