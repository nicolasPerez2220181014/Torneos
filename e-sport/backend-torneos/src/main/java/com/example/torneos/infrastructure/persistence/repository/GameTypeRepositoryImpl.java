package com.example.torneos.infrastructure.persistence.repository;

import com.example.torneos.domain.model.GameType;
import com.example.torneos.domain.repository.GameTypeRepository;
import com.example.torneos.infrastructure.persistence.mapper.GameTypeMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class GameTypeRepositoryImpl implements GameTypeRepository {

    private final JpaGameTypeRepository jpaRepository;
    private final GameTypeMapper mapper;

    public GameTypeRepositoryImpl(JpaGameTypeRepository jpaRepository, GameTypeMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public GameType save(GameType gameType) {
        var entity = mapper.toEntity(gameType);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<GameType> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<GameType> findByName(String name) {
        return jpaRepository.findByName(name).map(mapper::toDomain);
    }

    @Override
    public Page<GameType> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public List<GameType> findByActive(boolean active) {
        return jpaRepository.findByActive(active).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }
}