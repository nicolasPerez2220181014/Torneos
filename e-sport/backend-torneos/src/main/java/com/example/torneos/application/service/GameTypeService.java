package com.example.torneos.application.service;

import com.example.torneos.application.dto.request.CreateGameTypeRequest;
import com.example.torneos.application.dto.request.UpdateGameTypeRequest;
import com.example.torneos.application.dto.response.GameTypeResponse;
import com.example.torneos.domain.model.GameType;
import com.example.torneos.domain.repository.GameTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class GameTypeService {

    private final GameTypeRepository gameTypeRepository;

    public GameTypeService(GameTypeRepository gameTypeRepository) {
        this.gameTypeRepository = gameTypeRepository;
    }

    public GameTypeResponse create(CreateGameTypeRequest request) {
        if (gameTypeRepository.existsByName(request.name())) {
            throw new IllegalArgumentException("Ya existe un tipo de juego con el nombre: " + request.name());
        }

        GameType gameType = new GameType(request.name());
        GameType savedGameType = gameTypeRepository.save(gameType);
        
        return new GameTypeResponse(
            savedGameType.getId(),
            savedGameType.getName(),
            savedGameType.isActive()
        );
    }

    @Transactional(readOnly = true)
    public Page<GameTypeResponse> findAll(Pageable pageable) {
        return gameTypeRepository.findAll(pageable)
                .map(gameType -> new GameTypeResponse(
                    gameType.getId(),
                    gameType.getName(),
                    gameType.isActive()
                ));
    }

    @Transactional(readOnly = true)
    public GameTypeResponse findById(UUID id) {
        GameType gameType = gameTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de juego no encontrado con ID: " + id));
        
        return new GameTypeResponse(
            gameType.getId(),
            gameType.getName(),
            gameType.isActive()
        );
    }

    public GameTypeResponse update(UUID id, UpdateGameTypeRequest request) {
        GameType gameType = gameTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de juego no encontrado con ID: " + id));

        // Verificar si el nuevo nombre ya existe (excepto para el mismo tipo de juego)
        gameTypeRepository.findByName(request.name())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new IllegalArgumentException("Ya existe un tipo de juego con el nombre: " + request.name());
                    }
                });

        gameType.setName(request.name());
        gameType.setActive(request.active());
        
        GameType updatedGameType = gameTypeRepository.save(gameType);
        
        return new GameTypeResponse(
            updatedGameType.getId(),
            updatedGameType.getName(),
            updatedGameType.isActive()
        );
    }
}