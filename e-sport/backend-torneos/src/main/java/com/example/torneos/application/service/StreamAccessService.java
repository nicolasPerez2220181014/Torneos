package com.example.torneos.application.service;

import com.example.torneos.application.dto.request.StreamAccessRequestDto;
import com.example.torneos.application.dto.response.StreamAccessResponseDto;
import com.example.torneos.domain.model.StreamAccess;
import com.example.torneos.domain.model.Ticket;
import com.example.torneos.domain.model.Tournament;
import com.example.torneos.domain.repository.StreamAccessRepository;
import com.example.torneos.domain.repository.TicketRepository;
import com.example.torneos.domain.repository.TournamentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class StreamAccessService {

    private final StreamAccessRepository streamAccessRepository;
    private final TournamentRepository tournamentRepository;
    private final TicketRepository ticketRepository;
    private final AuditLogService auditLogService;

    public StreamAccessService(StreamAccessRepository streamAccessRepository,
                             TournamentRepository tournamentRepository,
                             TicketRepository ticketRepository,
                             AuditLogService auditLogService) {
        this.streamAccessRepository = streamAccessRepository;
        this.tournamentRepository = tournamentRepository;
        this.ticketRepository = ticketRepository;
        this.auditLogService = auditLogService;
    }

    public StreamAccessResponseDto requestAccess(UUID tournamentId, UUID userId, StreamAccessRequestDto request) {
        // Validar que el torneo existe
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado"));

        // Validar que el usuario no tenga ya acceso a este torneo
        if (streamAccessRepository.existsByTournamentIdAndUserId(tournamentId, userId)) {
            throw new IllegalArgumentException("El usuario ya tiene acceso a este torneo");
        }

        StreamAccess.AccessType accessType;
        UUID ticketId = null;

        try {
            accessType = StreamAccess.AccessType.valueOf(request.getAccessType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de acceso inválido. Debe ser FREE o PAID");
        }

        if (accessType == StreamAccess.AccessType.FREE) {
            // Validar regla: máximo 1 acceso gratuito por usuario
            int freeAccessCount = streamAccessRepository.countFreeAccessByUserId(userId);
            if (freeAccessCount >= 1) {
                throw new IllegalArgumentException("El usuario ya ha utilizado su acceso gratuito");
            }
        } else if (accessType == StreamAccess.AccessType.PAID) {
            // Validar que el torneo sea de pago
            if (!tournament.isPaid()) {
                throw new IllegalArgumentException("No se puede solicitar acceso PAID para un torneo gratuito");
            }

            // Validar que se proporcione código de ticket
            if (request.getTicketAccessCode() == null || request.getTicketAccessCode().trim().isEmpty()) {
                throw new IllegalArgumentException("Se requiere código de ticket para acceso PAID");
            }

            // Validar ticket
            Ticket ticket = ticketRepository.findByAccessCode(request.getTicketAccessCode())
                    .orElseThrow(() -> new IllegalArgumentException("Código de ticket inválido"));

            // Validar que el ticket pertenece al torneo correcto
            if (!ticket.getTournamentId().equals(tournamentId)) {
                throw new IllegalArgumentException("El ticket no pertenece a este torneo");
            }

            // Validar que el ticket pertenece al usuario
            if (!ticket.getUserId().equals(userId)) {
                throw new IllegalArgumentException("El ticket no pertenece a este usuario");
            }

            // Validar que el ticket esté activo
            if (ticket.getStatus() != Ticket.TicketStatus.ISSUED) {
                throw new IllegalArgumentException("El ticket no está disponible para uso");
            }

            ticketId = ticket.getId();
        }

        // Crear acceso
        StreamAccess streamAccess = new StreamAccess(tournamentId, userId, accessType, ticketId);
        StreamAccess savedAccess = streamAccessRepository.save(streamAccess);

        // Auditoría
        auditLogService.logEvent(
            com.example.torneos.domain.model.AuditLog.EventType.STREAM_ACCESS_GRANTED,
            com.example.torneos.domain.model.AuditLog.EntityType.STREAM,
            savedAccess.getId(),
            userId,
            String.format("Acceso %s otorgado al torneo", accessType.name())
        );

        return mapToResponse(savedAccess);
    }

    @Transactional(readOnly = true)
    public List<StreamAccessResponseDto> findByTournamentId(UUID tournamentId) {
        // Validar que el torneo existe
        tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado"));

        return streamAccessRepository.findByTournamentId(tournamentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StreamAccessResponseDto> findByUserId(UUID userId) {
        return streamAccessRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StreamAccessResponseDto findByTournamentIdAndUserId(UUID tournamentId, UUID userId) {
        StreamAccess access = streamAccessRepository.findByTournamentIdAndUserId(tournamentId, userId)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no tiene acceso a este torneo"));
        
        return mapToResponse(access);
    }

    private StreamAccessResponseDto mapToResponse(StreamAccess streamAccess) {
        StreamAccessResponseDto response = new StreamAccessResponseDto();
        response.setId(streamAccess.getId());
        response.setTournamentId(streamAccess.getTournamentId());
        response.setUserId(streamAccess.getUserId());
        response.setAccessType(streamAccess.getAccessType().name());
        response.setTicketId(streamAccess.getTicketId());
        response.setCreatedAt(streamAccess.getCreatedAt());
        return response;
    }
}