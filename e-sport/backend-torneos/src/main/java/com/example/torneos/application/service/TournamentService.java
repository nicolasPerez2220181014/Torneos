package com.example.torneos.application.service;

import com.example.torneos.application.dto.request.CreateTournamentRequest;
import com.example.torneos.application.dto.request.UpdateTournamentRequest;
import com.example.torneos.application.dto.response.TournamentResponse;
import com.example.torneos.domain.model.Tournament;
import com.example.torneos.domain.model.User;
import com.example.torneos.domain.repository.CategoryRepository;
import com.example.torneos.domain.repository.GameTypeRepository;
import com.example.torneos.domain.repository.TournamentRepository;
import com.example.torneos.domain.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final GameTypeRepository gameTypeRepository;
    private final AuditLogService auditLogService;

    public TournamentService(TournamentRepository tournamentRepository,
                           UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           GameTypeRepository gameTypeRepository,
                           AuditLogService auditLogService) {
        this.tournamentRepository = tournamentRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.gameTypeRepository = gameTypeRepository;
        this.auditLogService = auditLogService;
    }

    public TournamentResponse create(CreateTournamentRequest request, UUID organizerId) {
        // Validar que el organizador existe y tiene rol ORGANIZER
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new IllegalArgumentException("Organizador no encontrado"));
        
        if (organizer.getRole() != User.UserRole.ORGANIZER) {
            throw new IllegalArgumentException("Solo los usuarios con rol ORGANIZER pueden crear torneos");
        }

        // Validar que la categoría existe
        categoryRepository.findById(UUID.fromString(request.categoryId()))
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        // Validar que el tipo de juego existe
        gameTypeRepository.findById(UUID.fromString(request.gameTypeId()))
                .orElseThrow(() -> new IllegalArgumentException("Tipo de juego no encontrado"));

        // Validar fechas
        if (request.endDateTime().isBefore(request.startDateTime())) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        // Regla de negocio: máximo 2 torneos gratuitos activos por organizador
        if (!request.isPaid()) {
            long activeFreeCount = tournamentRepository.countByOrganizerIdAndIsPaidAndStatus(
                organizerId, false, Tournament.TournamentStatus.PUBLISHED);
            
            if (activeFreeCount >= 2) {
                throw new IllegalArgumentException("Un organizador solo puede tener máximo 2 torneos gratuitos activos");
            }
        }

        // Validar capacidad para torneos gratuitos
        if (!request.isPaid() && request.maxFreeCapacity() == null) {
            throw new IllegalArgumentException("Los torneos gratuitos deben tener una capacidad máxima definida");
        }

        Tournament tournament = new Tournament(
            organizerId,
            UUID.fromString(request.categoryId()),
            UUID.fromString(request.gameTypeId()),
            request.name(),
            request.description(),
            request.isPaid(),
            request.maxFreeCapacity(),
            request.startDateTime(),
            request.endDateTime()
        );

        Tournament savedTournament = tournamentRepository.save(tournament);
        
        // Auditoría
        auditLogService.logEvent(
            com.example.torneos.domain.model.AuditLog.EventType.TOURNAMENT_CREATED,
            com.example.torneos.domain.model.AuditLog.EntityType.TOURNAMENT,
            savedTournament.getId(),
            organizerId,
            String.format("Torneo '%s' creado", savedTournament.getName())
        );
        
        return mapToResponse(savedTournament);
    }

    @Transactional(readOnly = true)
    public Page<TournamentResponse> findAll(Pageable pageable) {
        return tournamentRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<TournamentResponse> findByFilters(Boolean isPaid, Tournament.TournamentStatus status,
                                                UUID categoryId, UUID gameTypeId, UUID organizerId,
                                                Pageable pageable) {
        return tournamentRepository.findByFilters(isPaid, status, categoryId, gameTypeId, organizerId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public TournamentResponse findById(UUID id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado con ID: " + id));
        
        return mapToResponse(tournament);
    }

    public TournamentResponse update(UUID id, UpdateTournamentRequest request, UUID userId) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado con ID: " + id));

        // Validar que el usuario puede modificar el torneo (organizador o subadmin)
        validateTournamentAccess(tournament, userId);

        // Validar que la categoría existe
        categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        // Validar que el tipo de juego existe
        gameTypeRepository.findById(request.gameTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de juego no encontrado"));

        // Validar fechas
        if (request.endDateTime().isBefore(request.startDateTime())) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de inicio");
        }

        // Actualizar campos
        tournament.setCategoryId(request.categoryId());
        tournament.setGameTypeId(request.gameTypeId());
        tournament.setName(request.name());
        tournament.setDescription(request.description());
        tournament.setPaid(request.isPaid());
        tournament.setMaxFreeCapacity(request.maxFreeCapacity());
        tournament.setStartDateTime(request.startDateTime());
        tournament.setEndDateTime(request.endDateTime());

        Tournament updatedTournament = tournamentRepository.save(tournament);
        
        // Auditoría
        auditLogService.logEvent(
            com.example.torneos.domain.model.AuditLog.EventType.TOURNAMENT_UPDATED,
            com.example.torneos.domain.model.AuditLog.EntityType.TOURNAMENT,
            updatedTournament.getId(),
            userId,
            String.format("Torneo '%s' actualizado", updatedTournament.getName())
        );
        
        return mapToResponse(updatedTournament);
    }

    public TournamentResponse publish(UUID id, UUID userId) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado con ID: " + id));

        // Validar que el usuario puede publicar el torneo
        validateTournamentAccess(tournament, userId);

        if (tournament.getStatus() != Tournament.TournamentStatus.DRAFT) {
            throw new IllegalArgumentException("Solo se pueden publicar torneos en estado DRAFT");
        }

        tournament.setStatus(Tournament.TournamentStatus.PUBLISHED);
        Tournament publishedTournament = tournamentRepository.save(tournament);
        
        // Auditoría
        auditLogService.logEvent(
            com.example.torneos.domain.model.AuditLog.EventType.TOURNAMENT_PUBLISHED,
            com.example.torneos.domain.model.AuditLog.EntityType.TOURNAMENT,
            publishedTournament.getId(),
            userId,
            String.format("Torneo '%s' publicado", publishedTournament.getName())
        );
        
        return mapToResponse(publishedTournament);
    }

    private void validateTournamentAccess(Tournament tournament, UUID userId) {
        // TODO: Implementar validación de subadmins cuando esté listo TournamentAdminService
        if (!tournament.getOrganizerId().equals(userId)) {
            throw new IllegalArgumentException("No tienes permisos para modificar este torneo");
        }
    }

    private TournamentResponse mapToResponse(Tournament tournament) {
        return new TournamentResponse(
            tournament.getId(),
            tournament.getOrganizerId(),
            tournament.getCategoryId(),
            tournament.getGameTypeId(),
            tournament.getName(),
            tournament.getDescription(),
            tournament.isPaid(),
            tournament.getMaxFreeCapacity(),
            tournament.getStartDateTime(),
            tournament.getEndDateTime(),
            TournamentResponse.TournamentStatus.valueOf(tournament.getStatus().name()),
            tournament.getCreatedAt(),
            tournament.getUpdatedAt()
        );
    }
}