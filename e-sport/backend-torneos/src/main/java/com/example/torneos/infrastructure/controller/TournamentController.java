package com.example.torneos.infrastructure.controller;

import com.example.torneos.application.dto.request.AssignSubAdminRequest;
import com.example.torneos.application.dto.request.CreateTournamentRequest;
import com.example.torneos.application.dto.request.UpdateTournamentRequest;
import com.example.torneos.application.dto.response.TournamentAdminResponse;
import com.example.torneos.application.dto.response.TournamentResponse;
import com.example.torneos.application.service.TournamentAdminService;
import com.example.torneos.application.service.TournamentService;
import com.example.torneos.domain.model.Tournament;
import com.example.torneos.infrastructure.web.ratelimit.RateLimit;
import com.example.torneos.infrastructure.web.versioning.ApiVersion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/tournaments")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Tournaments", description = "API para gestión de torneos")
@ApiVersion("v1")
@RateLimit(requests = 100, window = 1, timeUnit = TimeUnit.MINUTES)
public class TournamentController {

    private final TournamentService tournamentService;
    private final TournamentAdminService tournamentAdminService;

    public TournamentController(TournamentService tournamentService, 
                               TournamentAdminService tournamentAdminService) {
        this.tournamentService = tournamentService;
        this.tournamentAdminService = tournamentAdminService;
    }

    @PostMapping
    @Operation(summary = "Crear torneo", description = "Crea un nuevo torneo (solo ORGANIZER)")
    // @RateLimit(requests = 10, window = 1, timeUnit = TimeUnit.MINUTES)
    public ResponseEntity<TournamentResponse> create(
            @Valid @RequestBody CreateTournamentRequest request,
            @RequestHeader("X-USER-ID") String userIdHeader) {
        
        UUID organizerId = UUID.fromString(userIdHeader);
        TournamentResponse response = tournamentService.create(request, organizerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar torneos", description = "Obtiene todos los torneos con filtros opcionales")
    public ResponseEntity<Page<TournamentResponse>> findAll(
            @RequestParam(required = false) Boolean isPaid,
            @RequestParam(required = false) Tournament.TournamentStatus status,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID gameTypeId,
            @RequestParam(required = false) UUID organizerId,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        
        Page<TournamentResponse> response = tournamentService.findByFilters(
            isPaid, status, categoryId, gameTypeId, organizerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener torneo por ID", description = "Obtiene un torneo por su ID")
    public ResponseEntity<TournamentResponse> findById(
            @Parameter(description = "ID del torneo") @PathVariable UUID id) {
        TournamentResponse response = tournamentService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar torneo", description = "Actualiza un torneo existente (solo organizador/subadmin)")
    public ResponseEntity<TournamentResponse> update(
            @Parameter(description = "ID del torneo") @PathVariable UUID id,
            @Valid @RequestBody UpdateTournamentRequest request,
            @RequestHeader("X-USER-ID") String userIdHeader) {
        
        UUID userId = UUID.fromString(userIdHeader);
        TournamentResponse response = tournamentService.update(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "Publicar torneo", description = "Cambia el estado del torneo a PUBLISHED")
    public ResponseEntity<TournamentResponse> publish(
            @Parameter(description = "ID del torneo") @PathVariable UUID id,
            @RequestHeader("X-USER-ID") String userIdHeader) {
        
        UUID userId = UUID.fromString(userIdHeader);
        TournamentResponse response = tournamentService.publish(id, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/subadmins")
    @Operation(summary = "Asignar subadministrador", description = "Asigna un subadministrador al torneo (máximo 2)")
    public ResponseEntity<TournamentAdminResponse> assignSubAdmin(
            @Parameter(description = "ID del torneo") @PathVariable UUID id,
            @Valid @RequestBody AssignSubAdminRequest request,
            @RequestHeader("X-USER-ID") String userIdHeader) {
        
        UUID organizerId = UUID.fromString(userIdHeader);
        TournamentAdminResponse response = tournamentAdminService.assignSubAdmin(id, request, organizerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/subadmins")
    @Operation(summary = "Listar subadministradores", description = "Obtiene los subadministradores del torneo")
    public ResponseEntity<List<TournamentAdminResponse>> getSubAdmins(
            @Parameter(description = "ID del torneo") @PathVariable UUID id) {
        List<TournamentAdminResponse> response = tournamentAdminService.findByTournamentId(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{tournamentId}/subadmins/{subAdminId}")
    @Operation(summary = "Remover subadministrador", description = "Remueve un subadministrador del torneo")
    public ResponseEntity<Void> removeSubAdmin(
            @Parameter(description = "ID del torneo") @PathVariable UUID tournamentId,
            @Parameter(description = "ID del subadministrador") @PathVariable UUID subAdminId,
            @RequestHeader("X-USER-ID") String userIdHeader) {
        
        UUID organizerId = UUID.fromString(userIdHeader);
        tournamentAdminService.removeSubAdmin(tournamentId, subAdminId, organizerId);
        return ResponseEntity.noContent().build();
    }
}