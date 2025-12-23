package com.example.torneos.infrastructure.controller;

import com.example.torneos.application.dto.request.CreateTicketSaleStageRequest;
import com.example.torneos.application.dto.response.TicketSaleStageResponse;
import com.example.torneos.application.service.TicketSaleStageService;
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
@RequestMapping("/api/tournaments/{tournamentId}/stages")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Ticket Sale Stages", description = "API para gestión de etapas de venta de tickets")
public class TicketSaleStageController {

    private final TicketSaleStageService stageService;

    public TicketSaleStageController(TicketSaleStageService stageService) {
        this.stageService = stageService;
    }

    @PostMapping
    @Operation(summary = "Crear etapa de venta", description = "Crea una nueva etapa de venta para un torneo")
    public ResponseEntity<TicketSaleStageResponse> create(
            @Parameter(description = "ID del torneo") @PathVariable UUID tournamentId,
            @Valid @RequestBody CreateTicketSaleStageRequest request,
            @RequestHeader("X-USER-ID") String userIdHeader) {
        
        UUID userId = UUID.fromString(userIdHeader);
        TicketSaleStageResponse response = stageService.create(tournamentId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar etapas de venta", description = "Obtiene todas las etapas de venta de un torneo")
    public ResponseEntity<List<TicketSaleStageResponse>> findByTournamentId(
            @Parameter(description = "ID del torneo") @PathVariable UUID tournamentId) {
        List<TicketSaleStageResponse> response = stageService.findByTournamentId(tournamentId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{stageId}")
    @Operation(summary = "Actualizar etapa de venta", description = "Actualiza una etapa de venta existente")
    public ResponseEntity<TicketSaleStageResponse> update(
            @Parameter(description = "ID del torneo") @PathVariable UUID tournamentId,
            @Parameter(description = "ID de la etapa") @PathVariable UUID stageId,
            @Valid @RequestBody CreateTicketSaleStageRequest request,
            @RequestHeader("X-USER-ID") String userIdHeader) {
        
        UUID userId = UUID.fromString(userIdHeader);
        TicketSaleStageResponse response = stageService.update(stageId, request, userId);
        return ResponseEntity.ok(response);
    }
}