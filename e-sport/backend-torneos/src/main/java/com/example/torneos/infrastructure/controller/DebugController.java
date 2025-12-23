package com.example.torneos.infrastructure.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/debug")
@CrossOrigin(origins = "http://localhost:4200")
public class DebugController {

    @PostMapping("/tournament")
    public ResponseEntity<Map<String, Object>> debugTournament(@RequestBody Map<String, Object> data) {
        System.out.println("=== DEBUG TOURNAMENT DATA ===");
        data.forEach((key, value) -> {
            System.out.println(key + ": " + value + " (type: " + (value != null ? value.getClass().getSimpleName() : "null") + ")");
        });
        System.out.println("==============================");
        
        return ResponseEntity.ok(Map.of(
            "received", data,
            "status", "success"
        ));
    }
}