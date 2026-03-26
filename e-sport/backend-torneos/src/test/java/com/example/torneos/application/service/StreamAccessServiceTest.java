package com.example.torneos.application.service;

import com.example.torneos.application.dto.request.StreamAccessRequestDto;
import com.example.torneos.application.dto.response.StreamAccessResponseDto;
import com.example.torneos.domain.model.StreamAccess;
import com.example.torneos.domain.model.Ticket;
import com.example.torneos.domain.model.Tournament;
import com.example.torneos.domain.repository.StreamAccessRepository;
import com.example.torneos.domain.repository.TicketRepository;
import com.example.torneos.domain.repository.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StreamAccessServiceTest {

    @Mock
    private StreamAccessRepository streamAccessRepository;
    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private StreamAccessService streamAccessService;

    private UUID tournamentId;
    private UUID userId;
    private Tournament tournament;

    @BeforeEach
    void setUp() {
        tournamentId = UUID.randomUUID();
        userId = UUID.randomUUID();
        tournament = new Tournament(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 
            "Tournament", "Description", false, 100, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
    }

    @Test
    void requestAccess_shouldThrowException_whenUserExceedsFreeAccessLimit() {
        StreamAccessRequestDto request = new StreamAccessRequestDto();
        request.setAccessType("FREE");

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(streamAccessRepository.existsByTournamentIdAndUserId(tournamentId, userId)).thenReturn(false);
        when(streamAccessRepository.countFreeAccessByUserId(userId)).thenReturn(1);

        assertThrows(IllegalArgumentException.class, 
            () -> streamAccessService.requestAccess(tournamentId, userId, request));
        verify(streamAccessRepository, never()).save(any());
    }

    @Test
    void requestAccess_shouldGrantFreeAccess_whenUserHasNoFreeAccess() {
        StreamAccessRequestDto request = new StreamAccessRequestDto();
        request.setAccessType("FREE");

        StreamAccess streamAccess = new StreamAccess(tournamentId, userId, StreamAccess.AccessType.FREE, null);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(streamAccessRepository.existsByTournamentIdAndUserId(tournamentId, userId)).thenReturn(false);
        when(streamAccessRepository.countFreeAccessByUserId(userId)).thenReturn(0);
        when(streamAccessRepository.save(any(StreamAccess.class))).thenReturn(streamAccess);

        StreamAccessResponseDto response = streamAccessService.requestAccess(tournamentId, userId, request);

        assertNotNull(response);
        verify(streamAccessRepository).save(any(StreamAccess.class));
        verify(auditLogService).logEvent(any(), any(), any(), any(), any());
    }

    @Test
    void requestAccess_shouldThrowException_whenUserAlreadyHasAccess() {
        StreamAccessRequestDto request = new StreamAccessRequestDto();
        request.setAccessType("FREE");

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(streamAccessRepository.existsByTournamentIdAndUserId(tournamentId, userId)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, 
            () -> streamAccessService.requestAccess(tournamentId, userId, request));
    }

    @Test
    void requestAccess_shouldGrantPaidAccess_whenTicketIsValid() {
        Tournament paidTournament = new Tournament(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 
            "Paid Tournament", "Description", true, null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        
        String ticketCode = "TICKET-123";
        UUID ticketId = UUID.randomUUID();
        Ticket ticket = new Ticket(UUID.randomUUID(), tournamentId, userId, ticketCode);
        ticket.setStatus(Ticket.TicketStatus.ISSUED);

        StreamAccessRequestDto request = new StreamAccessRequestDto();
        request.setAccessType("PAID");
        request.setTicketAccessCode(ticketCode);

        StreamAccess streamAccess = new StreamAccess(tournamentId, userId, StreamAccess.AccessType.PAID, ticketId);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(paidTournament));
        when(streamAccessRepository.existsByTournamentIdAndUserId(tournamentId, userId)).thenReturn(false);
        when(ticketRepository.findByAccessCode(ticketCode)).thenReturn(Optional.of(ticket));
        when(streamAccessRepository.save(any(StreamAccess.class))).thenReturn(streamAccess);

        StreamAccessResponseDto response = streamAccessService.requestAccess(tournamentId, userId, request);

        assertNotNull(response);
        verify(streamAccessRepository).save(any(StreamAccess.class));
    }

    @Test
    void requestAccess_shouldThrowException_whenPaidAccessWithoutTicket() {
        Tournament paidTournament = new Tournament(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 
            "Paid Tournament", "Description", true, null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));

        StreamAccessRequestDto request = new StreamAccessRequestDto();
        request.setAccessType("PAID");

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(paidTournament));
        when(streamAccessRepository.existsByTournamentIdAndUserId(tournamentId, userId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, 
            () -> streamAccessService.requestAccess(tournamentId, userId, request));
    }
}
