package com.example.torneos.application.service;

import com.example.torneos.application.dto.request.BlockStreamRequestDto;
import com.example.torneos.application.dto.request.UpdateStreamUrlRequestDto;
import com.example.torneos.application.dto.response.StreamStatusResponseDto;
import com.example.torneos.domain.model.StreamAccess;
import com.example.torneos.domain.model.StreamLinkControl;
import com.example.torneos.domain.model.Tournament;
import com.example.torneos.domain.repository.StreamAccessRepository;
import com.example.torneos.domain.repository.StreamLinkControlRepository;
import com.example.torneos.domain.repository.TournamentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class StreamLinkControlService {

    private final StreamLinkControlRepository streamLinkControlRepository;
    private final StreamAccessRepository streamAccessRepository;
    private final TournamentRepository tournamentRepository;
    private final AuditLogService auditLogService;

    public StreamLinkControlService(StreamLinkControlRepository streamLinkControlRepository,
                                  StreamAccessRepository streamAccessRepository,
                                  TournamentRepository tournamentRepository,
                                  AuditLogService auditLogService) {
        this.streamLinkControlRepository = streamLinkControlRepository;
        this.streamAccessRepository = streamAccessRepository;
        this.tournamentRepository = tournamentRepository;
        this.auditLogService = auditLogService;
    }

    public void updateStreamUrl(UUID tournamentId, UUID organizerId, UpdateStreamUrlRequestDto request) {
        // Validar que el torneo existe y pertenece al organizador
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado"));

        if (!tournament.getOrganizerId().equals(organizerId)) {
            throw new IllegalArgumentException("Solo el organizador puede actualizar la URL del stream");
        }

        Optional<StreamLinkControl> existingControl = streamLinkControlRepository.findByTournamentId(tournamentId);
        
        if (existingControl.isPresent()) {
            StreamLinkControl control = existingControl.get();
            control.setStreamUrl(request.getStreamUrl());
            streamLinkControlRepository.save(control);
        } else {
            StreamLinkControl newControl = new StreamLinkControl(tournamentId, request.getStreamUrl());
            streamLinkControlRepository.save(newControl);
        }
    }

    public void blockStream(UUID tournamentId, UUID organizerId, BlockStreamRequestDto request) {
        // Validar que el torneo existe y pertenece al organizador
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado"));

        if (!tournament.getOrganizerId().equals(organizerId)) {
            throw new IllegalArgumentException("Solo el organizador puede bloquear el stream");
        }

        StreamLinkControl control = streamLinkControlRepository.findByTournamentId(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró configuración de stream para este torneo"));

        control.block(request.getBlockReason());
        streamLinkControlRepository.save(control);
        
        // Auditoría
        auditLogService.logEvent(
            com.example.torneos.domain.model.AuditLog.EventType.STREAM_BLOCKED,
            com.example.torneos.domain.model.AuditLog.EntityType.STREAM,
            control.getId(),
            organizerId,
            String.format("Stream bloqueado: %s", request.getBlockReason())
        );
    }

    public void unblockStream(UUID tournamentId, UUID organizerId) {
        // Validar que el torneo existe y pertenece al organizador
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado"));

        if (!tournament.getOrganizerId().equals(organizerId)) {
            throw new IllegalArgumentException("Solo el organizador puede desbloquear el stream");
        }

        StreamLinkControl control = streamLinkControlRepository.findByTournamentId(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró configuración de stream para este torneo"));

        control.unblock();
        streamLinkControlRepository.save(control);
        
        // Auditoría
        auditLogService.logEvent(
            com.example.torneos.domain.model.AuditLog.EventType.STREAM_UNBLOCKED,
            com.example.torneos.domain.model.AuditLog.EntityType.STREAM,
            control.getId(),
            organizerId,
            "Stream desbloqueado"
        );
    }

    @Transactional(readOnly = true)
    public StreamStatusResponseDto getStreamStatus(UUID tournamentId, UUID userId) {
        // Validar que el torneo existe
        tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado"));

        StreamStatusResponseDto response = new StreamStatusResponseDto();
        response.setTournamentId(tournamentId);

        // Obtener configuración del stream
        Optional<StreamLinkControl> controlOpt = streamLinkControlRepository.findByTournamentId(tournamentId);
        if (controlOpt.isPresent()) {
            StreamLinkControl control = controlOpt.get();
            response.setBlocked(control.isBlocked());
            response.setBlockReason(control.getBlockReason());
            response.setBlockedAt(control.getBlockedAt());
            
            // Solo mostrar URL si no está bloqueado y el usuario tiene acceso
            if (!control.isBlocked()) {
                response.setStreamUrl(control.getStreamUrl());
            }
        }

        // Verificar si el usuario tiene acceso
        Optional<StreamAccess> accessOpt = streamAccessRepository.findByTournamentIdAndUserId(tournamentId, userId);
        if (accessOpt.isPresent()) {
            StreamAccess access = accessOpt.get();
            response.setHasAccess(true);
            response.setAccessType(access.getAccessType().name());
        } else {
            response.setHasAccess(false);
        }

        return response;
    }
}