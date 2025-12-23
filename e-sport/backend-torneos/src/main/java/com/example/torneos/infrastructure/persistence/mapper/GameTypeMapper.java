package com.example.torneos.infrastructure.persistence.mapper;

import com.example.torneos.domain.model.GameType;
import com.example.torneos.infrastructure.persistence.entity.GameTypeEntity;
import org.springframework.stereotype.Component;

@Component
public class GameTypeMapper {

    public GameType toDomain(GameTypeEntity entity) {
        if (entity == null) return null;
        return new GameType(entity.getId(), entity.getName(), entity.isActive());
    }

    public GameTypeEntity toEntity(GameType domain) {
        if (domain == null) return null;
        GameTypeEntity entity = new GameTypeEntity(domain.getName(), domain.isActive());
        entity.setId(domain.getId());
        return entity;
    }
}