package com.example.torneos.application.service;

import com.example.torneos.application.dto.request.CreateTicketSaleStageRequest;
import com.example.torneos.application.dto.response.TicketSaleStageResponse;
import com.example.torneos.domain.model.TicketSaleStage;
import com.example.torneos.domain.model.Tournament;
import com.example.torneos.domain.repository.TicketSaleStageRepository;
import com.example.torneos.domain.repository.TournamentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TicketSaleStageService {

    private final TicketSaleStageRepository stageRepository;
    private final TournamentRepository tournamentRepository;

    public TicketSaleStageService(TicketSaleStageRepository stageRepository, TournamentRepository tournamentRepository) {
        this.stageRepository = stageRepository;
        this.tournamentRepository = tournamentRepository;
    }

    public TicketSaleStageResponse create(UUID tournamentId, CreateTicketSaleStageRequest request, UUID userId) {
        // Validar que el torneo existe
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado"));

        // Validar que el usuario puede crear etapas (organizador o subadmin)
        if (!tournament.getOrganizerId().equals(userId)) {
            throw new IllegalArgumentException("Solo el organizador puede crear etapas de venta");
        }

        // Validar que el torneo es de pago
        if (!tournament.isPaid()) {
            throw new IllegalArgumentException("Solo los torneos de pago pueden tener etapas de venta");
        }

        // Validar que no existe ya una etapa del mismo tipo
        if (stageRepository.existsByTournamentIdAndStageType(tournamentId, 
                TicketSaleStage.StageType.valueOf(request.stageType().name()))) {
            throw new IllegalArgumentException("Ya existe una etapa de tipo " + request.stageType() + " para este torneo");
        }

        // Validar fechas
        if (request.endDateTime().isBefore(request.startDateTime())) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        TicketSaleStage stage = new TicketSaleStage(
            tournamentId,
            TicketSaleStage.StageType.valueOf(request.stageType().name()),
            request.price(),
            request.capacity(),
            request.startDateTime(),
            request.endDateTime()
        );

        TicketSaleStage savedStage = stageRepository.save(stage);
        return mapToResponse(savedStage);
    }

    @Transactional(readOnly = true)
    public List<TicketSaleStageResponse> findByTournamentId(UUID tournamentId) {
        // Validar que el torneo existe
        tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado"));

        return stageRepository.findByTournamentId(tournamentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TicketSaleStageResponse update(UUID stageId, CreateTicketSaleStageRequest request, UUID userId) {
        TicketSaleStage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new IllegalArgumentException("Etapa de venta no encontrada"));

        // Validar que el torneo existe y el usuario tiene permisos
        Tournament tournament = tournamentRepository.findById(stage.getTournamentId())
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado"));

        if (!tournament.getOrganizerId().equals(userId)) {
            throw new IllegalArgumentException("Solo el organizador puede modificar etapas de venta");
        }

        // Validar fechas
        if (request.endDateTime().isBefore(request.startDateTime())) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        stage.setPrice(request.price());
        stage.setCapacity(request.capacity());
        stage.setStartDateTime(request.startDateTime());
        stage.setEndDateTime(request.endDateTime());

        TicketSaleStage updatedStage = stageRepository.save(stage);
        return mapToResponse(updatedStage);
    }

    private TicketSaleStageResponse mapToResponse(TicketSaleStage stage) {
        return new TicketSaleStageResponse(
            stage.getId(),
            stage.getTournamentId(),
            TicketSaleStageResponse.StageType.valueOf(stage.getStageType().name()),
            stage.getPrice(),
            stage.getCapacity(),
            stage.getStartDateTime(),
            stage.getEndDateTime(),
            stage.isActive()
        );
    }
}