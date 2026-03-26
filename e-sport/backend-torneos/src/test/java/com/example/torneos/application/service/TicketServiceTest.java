package com.example.torneos.application.service;

import com.example.torneos.application.dto.response.TicketResponse;
import com.example.torneos.domain.model.Ticket;
import com.example.torneos.domain.repository.TicketRepository;
import com.example.torneos.domain.repository.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private TicketService ticketService;

    private UUID tournamentId;
    private UUID userId;
    private UUID orderId;
    private String accessCode;

    @BeforeEach
    void setUp() {
        tournamentId = UUID.randomUUID();
        userId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        accessCode = "UNIQUE-ACCESS-CODE-123";
    }

    @Test
    void validateTicket_shouldMarkAsUsed_whenTicketIsValid() {
        Ticket ticket = new Ticket(orderId, tournamentId, userId, accessCode);
        ticket.setStatus(Ticket.TicketStatus.ISSUED);

        when(ticketRepository.findByAccessCode(accessCode)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        TicketResponse response = ticketService.validateTicket(accessCode);

        assertNotNull(response);
        assertEquals(Ticket.TicketStatus.USED, ticket.getStatus());
        assertNotNull(ticket.getUsedAt());
        verify(ticketRepository).save(ticket);
        verify(auditLogService).logEvent(any(), any(), any(), any(), any());
    }

    @Test
    void validateTicket_shouldThrowException_whenTicketAlreadyUsed() {
        Ticket ticket = new Ticket(orderId, tournamentId, userId, accessCode);
        ticket.setStatus(Ticket.TicketStatus.USED);

        when(ticketRepository.findByAccessCode(accessCode)).thenReturn(Optional.of(ticket));

        assertThrows(IllegalArgumentException.class, () -> ticketService.validateTicket(accessCode));
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void validateTicket_shouldThrowException_whenTicketCancelled() {
        Ticket ticket = new Ticket(orderId, tournamentId, userId, accessCode);
        ticket.setStatus(Ticket.TicketStatus.CANCELLED);

        when(ticketRepository.findByAccessCode(accessCode)).thenReturn(Optional.of(ticket));

        assertThrows(IllegalArgumentException.class, () -> ticketService.validateTicket(accessCode));
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void validateTicket_shouldThrowException_whenAccessCodeInvalid() {
        when(ticketRepository.findByAccessCode(accessCode)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> ticketService.validateTicket(accessCode));
    }

    @Test
    void findByAccessCode_shouldReturnTicket_whenExists() {
        Ticket ticket = new Ticket(orderId, tournamentId, userId, accessCode);

        when(ticketRepository.findByAccessCode(accessCode)).thenReturn(Optional.of(ticket));

        TicketResponse response = ticketService.findByAccessCode(accessCode);

        assertNotNull(response);
        assertEquals(accessCode, response.accessCode());
    }
}
