package com.example.torneos.application.service;

import com.example.torneos.application.dto.response.TicketResponse;
import com.example.torneos.domain.model.Ticket;
import com.example.torneos.domain.repository.TicketRepository;
import com.example.torneos.domain.repository.TournamentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TournamentRepository tournamentRepository;
    private final AuditLogService auditLogService;

    public TicketService(TicketRepository ticketRepository, 
                        TournamentRepository tournamentRepository,
                        AuditLogService auditLogService) {
        this.ticketRepository = ticketRepository;
        this.tournamentRepository = tournamentRepository;
        this.auditLogService = auditLogService;
    }

    public TicketResponse validateTicket(String accessCode) {
        Ticket ticket = ticketRepository.findByAccessCode(accessCode)
                .orElseThrow(() -> new IllegalArgumentException("Código de acceso inválido"));

        // Validar que el ticket no ha sido usado
        if (ticket.getStatus() == Ticket.TicketStatus.USED) {
            throw new IllegalArgumentException("El ticket ya ha sido utilizado");
        }

        // Validar que el ticket no ha sido cancelado
        if (ticket.getStatus() == Ticket.TicketStatus.CANCELLED) {
            throw new IllegalArgumentException("El ticket ha sido cancelado");
        }

        // Marcar como usado
        ticket.setStatus(Ticket.TicketStatus.USED);
        ticket.setUsedAt(LocalDateTime.now());
        
        Ticket updatedTicket = ticketRepository.save(ticket);
        
        // Auditoría
        auditLogService.logEvent(
            com.example.torneos.domain.model.AuditLog.EventType.TICKET_VALIDATED,
            com.example.torneos.domain.model.AuditLog.EntityType.TICKET,
            updatedTicket.getId(),
            updatedTicket.getUserId(),
            String.format("Ticket validado: %s", accessCode)
        );
        
        return mapToResponse(updatedTicket);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> findByTournamentIdAndUserId(UUID tournamentId, UUID userId) {
        // Validar que el torneo existe
        tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado"));

        return ticketRepository.findByTournamentIdAndUserId(tournamentId, userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> findByTournamentId(UUID tournamentId) {
        // Validar que el torneo existe
        tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado"));

        return ticketRepository.findByTournamentId(tournamentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TicketResponse findByAccessCode(String accessCode) {
        Ticket ticket = ticketRepository.findByAccessCode(accessCode)
                .orElseThrow(() -> new IllegalArgumentException("Código de acceso no encontrado"));
        
        return mapToResponse(ticket);
    }

    private TicketResponse mapToResponse(Ticket ticket) {
        return new TicketResponse(
            ticket.getId(),
            ticket.getOrderId(),
            ticket.getTournamentId(),
            ticket.getUserId(),
            ticket.getAccessCode(),
            TicketResponse.TicketStatus.valueOf(ticket.getStatus().name()),
            ticket.getUsedAt(),
            ticket.getCreatedAt()
        );
    }
}