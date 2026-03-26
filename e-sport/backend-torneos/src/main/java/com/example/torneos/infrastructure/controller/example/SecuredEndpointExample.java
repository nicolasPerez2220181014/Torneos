package com.example.torneos.infrastructure.controller.example;

import com.example.torneos.domain.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Ejemplo de controlador con seguridad JWT y @PreAuthorize
 * 
 * ROLES:
 * - USER: Usuario regular
 * - ORGANIZER: Organizador de torneos
 * - SUBADMIN: Subadministrador de torneos
 */
@RestController
@RequestMapping("/api/example")
public class SecuredEndpointExample {

    @GetMapping("/public")
    public ResponseEntity<Map<String, String>> publicEndpoint() {
        return ResponseEntity.ok(Map.of("message", "Endpoint público - sin autenticación"));
    }

    @GetMapping("/authenticated")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> authenticatedEndpoint(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(Map.of(
            "message", "Endpoint autenticado",
            "user", user.getEmail(),
            "role", user.getRole()
        ));
    }

    @PostMapping("/organizer-only")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<Map<String, String>> organizerOnly(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(Map.of(
            "message", "Solo ORGANIZER puede acceder",
            "organizer", user.getEmail()
        ));
    }

    @PostMapping("/organizer-or-subadmin")
    @PreAuthorize("hasAnyRole('ORGANIZER', 'SUBADMIN')")
    public ResponseEntity<Map<String, String>> organizerOrSubadmin(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(Map.of(
            "message", "ORGANIZER o SUBADMIN pueden acceder",
            "user", user.getEmail()
        ));
    }

    @GetMapping("/user-only")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, String>> userOnly(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(Map.of(
            "message", "Solo USER puede acceder",
            "user", user.getEmail()
        ));
    }
}
