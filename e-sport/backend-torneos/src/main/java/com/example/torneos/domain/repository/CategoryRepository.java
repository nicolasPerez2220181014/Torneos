package com.example.torneos.domain.repository;

import com.example.torneos.domain.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findById(UUID id);
    Optional<Category> findByName(String name);
    Page<Category> findAll(Pageable pageable);
    List<Category> findByActive(boolean active);
    void deleteById(UUID id);
    boolean existsByName(String name);
}