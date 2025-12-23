package com.example.torneos.infrastructure.persistence.repository;

import com.example.torneos.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
    List<UserEntity> findByRole(UserEntity.UserRole role);
    boolean existsByEmail(String email);
    Page<UserEntity> findAll(Pageable pageable);
    
    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.role = :role AND u.id != :excludeId")
    long countByRoleAndIdNot(@Param("role") UserEntity.UserRole role, @Param("excludeId") UUID excludeId);
}