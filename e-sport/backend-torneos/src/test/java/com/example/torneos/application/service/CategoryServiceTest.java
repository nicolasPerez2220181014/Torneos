package com.example.torneos.application.service;

import com.example.torneos.application.dto.request.CreateCategoryRequest;
import com.example.torneos.application.dto.request.UpdateCategoryRequest;
import com.example.torneos.application.dto.response.CategoryResponse;
import com.example.torneos.domain.model.Category;
import com.example.torneos.domain.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();
        category = new Category(categoryId, "Test Category", true);
    }

    @Test
    void create_ShouldCreateCategory_WhenNameDoesNotExist() {
        // Given
        CreateCategoryRequest request = new CreateCategoryRequest("New Category");
        when(categoryRepository.existsByName(anyString())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // When
        CategoryResponse response = categoryService.create(request);

        // Then
        assertNotNull(response);
        assertEquals(category.getId(), response.id());
        assertEquals(category.getName(), response.name());
        assertTrue(response.active());
        verify(categoryRepository).existsByName("New Category");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void create_ShouldThrowException_WhenNameAlreadyExists() {
        // Given
        CreateCategoryRequest request = new CreateCategoryRequest("Existing Category");
        when(categoryRepository.existsByName(anyString())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> categoryService.create(request)
        );
        assertEquals("Ya existe una categoría con el nombre: Existing Category", exception.getMessage());
        verify(categoryRepository).existsByName("Existing Category");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void findById_ShouldReturnCategory_WhenExists() {
        // Given
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // When
        CategoryResponse response = categoryService.findById(categoryId);

        // Then
        assertNotNull(response);
        assertEquals(category.getId(), response.id());
        assertEquals(category.getName(), response.name());
        assertEquals(category.isActive(), response.active());
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    void findById_ShouldThrowException_WhenNotExists() {
        // Given
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> categoryService.findById(categoryId)
        );
        assertEquals("Categoría no encontrada con ID: " + categoryId, exception.getMessage());
        verify(categoryRepository).findById(categoryId);
    }

    @Test
    void update_ShouldUpdateCategory_WhenValidRequest() {
        // Given
        UpdateCategoryRequest request = new UpdateCategoryRequest("Updated Category", false);
        Category updatedCategory = new Category(categoryId, "Updated Category", false);
        
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.findByName("Updated Category")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        // When
        CategoryResponse response = categoryService.update(categoryId, request);

        // Then
        assertNotNull(response);
        assertEquals(categoryId, response.id());
        assertEquals("Updated Category", response.name());
        assertFalse(response.active());
        verify(categoryRepository).findById(categoryId);
        verify(categoryRepository).findByName("Updated Category");
        verify(categoryRepository).save(any(Category.class));
    }
}