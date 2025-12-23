package com.example.torneos.infrastructure.controller;

import com.example.torneos.application.dto.request.CreateUserRequest;
import com.example.torneos.application.dto.request.UpdateUserRequest;
import com.example.torneos.application.dto.response.UserResponse;
import com.example.torneos.application.service.UserService;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Users", description = "API para gestión de usuarios")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario en el sistema")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios con paginación")
    public ResponseEntity<Page<UserResponse>> findAll(
            @PageableDefault(size = 20, sort = "fullName") Pageable pageable) {
        Page<UserResponse> response = userService.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Obtiene un usuario por su ID")
    public ResponseEntity<UserResponse> findById(
            @Parameter(description = "ID del usuario") @PathVariable UUID id) {
        try {
            UserResponse response = userService.findById(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw e; // Re-throw to be handled by GlobalExceptionHandler
        }
    }

    @GetMapping("/email/{email:.+}")
    @Operation(summary = "Obtener usuario por email", description = "Obtiene un usuario por su email")
    public ResponseEntity<UserResponse> findByEmail(
            @Parameter(description = "Email del usuario") @PathVariable String email) {
        try {
            UserResponse response = userService.findByEmail(email);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw e; // Re-throw to be handled by GlobalExceptionHandler
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza un usuario existente")
    public ResponseEntity<UserResponse> update(
            @Parameter(description = "ID del usuario") @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.update(id, request);
        return ResponseEntity.ok(response);
    }
}