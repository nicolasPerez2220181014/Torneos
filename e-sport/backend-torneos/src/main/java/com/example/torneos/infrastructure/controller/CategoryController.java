package com.example.torneos.infrastructure.controller;

import com.example.torneos.application.dto.request.CreateCategoryRequest;
import com.example.torneos.application.dto.request.UpdateCategoryRequest;
import com.example.torneos.application.dto.response.CategoryResponse;
import com.example.torneos.application.service.CategoryService;
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
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Categories", description = "API para gestión de categorías")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @Operation(summary = "Crear categoría", description = "Crea una nueva categoría")
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse response = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar categorías", description = "Obtiene todas las categorías con paginación")
    public ResponseEntity<Page<CategoryResponse>> findAll(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        Page<CategoryResponse> response = categoryService.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/simple")
    @Operation(summary = "Listar categorías simple", description = "Obtiene todas las categorías sin paginación")
    public ResponseEntity<java.util.List<CategoryResponse>> findAllSimple() {
        Page<CategoryResponse> page = categoryService.findAll(Pageable.unpaged());
        return ResponseEntity.ok(page.getContent());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoría", description = "Obtiene una categoría por su ID")
    public ResponseEntity<CategoryResponse> findById(@PathVariable UUID id) {
        CategoryResponse response = categoryService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría", description = "Actualiza una categoría existente")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        CategoryResponse response = categoryService.update(id, request);
        return ResponseEntity.ok(response);
    }
}