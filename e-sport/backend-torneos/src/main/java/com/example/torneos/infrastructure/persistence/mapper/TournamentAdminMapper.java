package com.example.torneos.infrastructure.persistence.mapper;

import com.example.torneos.domain.model.TournamentAdmin;
import com.example.torneos.infrastructure.persistence.entity.TournamentAdminEntity;
import org.springframework.stereotype.Component;

@Component
public class TournamentAdminMapper {

    public TournamentAdmin toDomain(TournamentAdminEntity entity) {
        if (entity == null) return null;
        
        TournamentAdmin admin = new TournamentAdmin(entity.getTournamentId(), entity.getSubAdminUserId());
        admin.setId(entity.getId());
        admin.setCreatedAt(entity.getCreatedAt());
        return admin;
    }

    public TournamentAdminEntity toEntity(TournamentAdmin domain) {
        if (domain == null) return null;
        
        TournamentAdminEntity entity = new TournamentAdminEntity(domain.getTournamentId(), domain.getSubAdminUserId());
        entity.setId(domain.getId());
        if (domain.getCreatedAt() != null) {
            entity.setCreatedAt(domain.getCreatedAt());
        }
        return entity;
    }
}