package com.example.torneos.infrastructure.persistence.repository;

import com.example.torneos.infrastructure.persistence.entity.GameTypeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaGameTypeRepository extends JpaRepository<GameTypeEntity, UUID> {
    Optional<GameTypeEntity> findByName(String name);
    List<GameTypeEntity> findByActive(boolean active);
    boolean existsByName(String name);
    Page<GameTypeEntity> findAll(Pageable pageable);
}