package com.example.torneos.infrastructure.persistence.mapper;

import com.example.torneos.domain.model.User;
import com.example.torneos.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;
        
        User user = new User(entity.getEmail(), entity.getFullName(), 
                           mapRole(entity.getRole()));
        user.setId(entity.getId());
        user.setCreatedAt(entity.getCreatedAt());
        user.setUpdatedAt(entity.getUpdatedAt());
        return user;
    }

    public UserEntity toEntity(User domain) {
        if (domain == null) return null;
        
        UserEntity entity = new UserEntity(domain.getEmail(), domain.getFullName(), 
                                         mapRole(domain.getRole()));
        entity.setId(domain.getId());
        if (domain.getCreatedAt() != null) {
            entity.setCreatedAt(domain.getCreatedAt());
        }
        if (domain.getUpdatedAt() != null) {
            entity.setUpdatedAt(domain.getUpdatedAt());
        }
        return entity;
    }

    private User.UserRole mapRole(UserEntity.UserRole entityRole) {
        return User.UserRole.valueOf(entityRole.name());
    }

    private UserEntity.UserRole mapRole(User.UserRole domainRole) {
        return UserEntity.UserRole.valueOf(domainRole.name());
    }
}