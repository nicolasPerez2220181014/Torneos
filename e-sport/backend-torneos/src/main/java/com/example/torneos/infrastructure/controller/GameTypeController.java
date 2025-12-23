package com.example.torneos.infrastructure.controller;

import com.example.torneos.application.dto.request.CreateGameTypeRequest;
import com.example.torneos.application.dto.request.UpdateGameTypeRequest;
import com.example.torneos.application.dto.response.GameTypeResponse;
import com.example.torneos.application.service.GameTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/game-types")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Game Types", description = "API para gestión de tipos de juego")
public class GameTypeController {

    private final GameTypeService gameTypeService;

    public GameTypeController(GameTypeService gameTypeService) {
        this.gameTypeService = gameTypeService;
    }

    @PostMapping
    @Operation(summary = "Crear tipo de juego", description = "Crea un nuevo tipo de juego")
    public ResponseEntity<GameTypeResponse> create(@Valid @RequestBody CreateGameTypeRequest request) {
        GameTypeResponse response = gameTypeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar tipos de juego", description = "Obtiene todos los tipos de juego con paginación")
    public ResponseEntity<Page<GameTypeResponse>> findAll(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<GameTypeResponse> response = gameTypeService.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/simple")
    @Operation(summary = "Listar tipos de juego simple", description = "Obtiene todos los tipos de juego sin paginación")
    public ResponseEntity<java.util.List<GameTypeResponse>> findAllSimple() {
        Page<GameTypeResponse> page = gameTypeService.findAll(Pageable.unpaged());
        return ResponseEntity.ok(page.getContent());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener tipo de juego", description = "Obtiene un tipo de juego por su ID")
    public ResponseEntity<GameTypeResponse> findById(@PathVariable UUID id) {
        GameTypeResponse response = gameTypeService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar tipo de juego", description = "Actualiza un tipo de juego existente")
    public ResponseEntity<GameTypeResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateGameTypeRequest request) {
        GameTypeResponse response = gameTypeService.update(id, request);
        return ResponseEntity.ok(response);
    }
}