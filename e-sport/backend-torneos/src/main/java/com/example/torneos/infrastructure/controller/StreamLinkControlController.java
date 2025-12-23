package com.example.torneos.infrastructure.controller;

import com.example.torneos.application.dto.request.BlockStreamRequestDto;
import com.example.torneos.application.dto.request.UpdateStreamUrlRequestDto;
import com.example.torneos.application.dto.response.StreamStatusResponseDto;
import com.example.torneos.application.service.StreamLinkControlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tournaments/{tournamentId}/stream")
@Tag(name = "Stream Control", description = "Control de URLs y bloqueo de streams")
public class StreamLinkControlController {

    private final StreamLinkControlService streamLinkControlService;

    public StreamLinkControlController(StreamLinkControlService streamLinkControlService) {
        this.streamLinkControlService = streamLinkControlService;
    }

    @PutMapping("/url")
    @Operation(summary = "Actualizar URL del stream", 
               description = "Permite al organizador actualizar la URL del stream del torneo")
    public ResponseEntity<Void> updateStreamUrl(
            @Parameter(description = "ID del torneo") @PathVariable UUID tournamentId,
            @Parameter(description = "ID del organizador") @RequestHeader("X-USER-ID") UUID organizerId,
            @Valid @RequestBody UpdateStreamUrlRequestDto request) {
        
        streamLinkControlService.updateStreamUrl(tournamentId, organizerId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/block")
    @Operation(summary = "Bloquear stream", 
               description = "Permite al organizador bloquear el stream con un motivo")
    public ResponseEntity<Void> blockStream(
            @Parameter(description = "ID del torneo") @PathVariable UUID tournamentId,
            @Parameter(description = "ID del organizador") @RequestHeader("X-USER-ID") UUID organizerId,
            @Valid @RequestBody BlockStreamRequestDto request) {
        
        streamLinkControlService.blockStream(tournamentId, organizerId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unblock")
    @Operation(summary = "Desbloquear stream", 
               description = "Permite al organizador desbloquear el stream")
    public ResponseEntity<Void> unblockStream(
            @Parameter(description = "ID del torneo") @PathVariable UUID tournamentId,
            @Parameter(description = "ID del organizador") @RequestHeader("X-USER-ID") UUID organizerId) {
        
        streamLinkControlService.unblockStream(tournamentId, organizerId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/status")
    @Operation(summary = "Obtener estado del stream", 
               description = "Obtiene el estado del stream incluyendo URL (si tiene acceso), bloqueo y acceso del usuario")
    public ResponseEntity<StreamStatusResponseDto> getStreamStatus(
            @Parameter(description = "ID del torneo") @PathVariable UUID tournamentId,
            @Parameter(description = "ID del usuario") @RequestHeader("X-USER-ID") UUID userId) {
        
        StreamStatusResponseDto response = streamLinkControlService.getStreamStatus(tournamentId, userId);
        return ResponseEntity.ok(response);
    }
}