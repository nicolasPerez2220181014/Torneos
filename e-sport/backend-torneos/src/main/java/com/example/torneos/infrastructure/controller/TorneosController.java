package com.example.torneos.infrastructure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Torneos", description = "API para gestión de torneos")
public class TorneosController {

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Verifica que la API esté funcionando")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = Map.of(
            "status", "UP",
            "message", "API de torneos funcionando correctamente",
            "timestamp", System.currentTimeMillis()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/torneos")
    @Operation(summary = "Obtener lista de torneos", description = "Retorna la lista de todos los torneos disponibles")
    public ResponseEntity<Map<String, Object>> getTorneos() {
        // Respuesta básica para testing de conectividad
        Map<String, Object> response = Map.of(
            "message", "API de torneos funcionando correctamente",
            "data", Collections.emptyList()
        );
        return ResponseEntity.ok(response);
    }
}