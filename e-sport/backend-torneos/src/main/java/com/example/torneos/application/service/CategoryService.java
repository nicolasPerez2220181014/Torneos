package com.example.torneos.application.service;

import com.example.torneos.application.dto.request.CreateCategoryRequest;
import com.example.torneos.application.dto.request.UpdateCategoryRequest;
import com.example.torneos.application.dto.response.CategoryResponse;
import com.example.torneos.domain.model.Category;
import com.example.torneos.domain.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CategoryResponse create(CreateCategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + request.name());
        }

        Category category = new Category(request.name());
        Category savedCategory = categoryRepository.save(category);
        
        return new CategoryResponse(
            savedCategory.getId(),
            savedCategory.getName(),
            savedCategory.isActive()
        );
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(category -> new CategoryResponse(
                    category.getId(),
                    category.getName(),
                    category.isActive()
                ));
    }

    @Transactional(readOnly = true)
    public CategoryResponse findById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));
        
        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.isActive()
        );
    }

    public CategoryResponse update(UUID id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));

        // Verificar si el nuevo nombre ya existe (excepto para la misma categoría)
        categoryRepository.findByName(request.name())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + request.name());
                    }
                });

        category.setName(request.name());
        category.setActive(request.active());
        
        Category updatedCategory = categoryRepository.save(category);
        
        return new CategoryResponse(
            updatedCategory.getId(),
            updatedCategory.getName(),
            updatedCategory.isActive()
        );
    }
}