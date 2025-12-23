package com.example.torneos.domain.repository;

import com.example.torneos.domain.model.GameType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameTypeRepository {
    GameType save(GameType gameType);
    Optional<GameType> findById(UUID id);
    Optional<GameType> findByName(String name);
    Page<GameType> findAll(Pageable pageable);
    List<GameType> findByActive(boolean active);
    void deleteById(UUID id);
    boolean existsByName(String name);
}