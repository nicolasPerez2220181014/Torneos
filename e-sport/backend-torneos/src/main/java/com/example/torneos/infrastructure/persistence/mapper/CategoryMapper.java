package com.example.torneos.infrastructure.persistence.mapper;

import com.example.torneos.domain.model.Category;
import com.example.torneos.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toDomain(CategoryEntity entity) {
        if (entity == null) return null;
        return new Category(entity.getId(), entity.getName(), entity.isActive());
    }

    public CategoryEntity toEntity(Category domain) {
        if (domain == null) return null;
        CategoryEntity entity = new CategoryEntity(domain.getName(), domain.isActive());
        entity.setId(domain.getId());
        return entity;
    }
}