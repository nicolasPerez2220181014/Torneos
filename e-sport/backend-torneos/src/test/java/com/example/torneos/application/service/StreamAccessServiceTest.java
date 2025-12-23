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
    private StreamAccessRequestDto freeAccessRequest;
    private StreamAccessRequestDto paidAccessRequest;

    @BeforeEach
    void setUp() {
        tournamentId = UUID.randomUUID();
        userId = UUID.randomUUID();
        
        tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setPaid(true);
        
        freeAccessRequest = new StreamAccessRequestDto();
        freeAccessRequest.setAccessType("FREE");
        
        paidAccessRequest = new StreamAccessRequestDto();
        paidAccessRequest.setAccessType("PAID");
        paidAccessRequest.setTicketAccessCode("TKT-12345678");
    }

    @Test
    void requestAccess_FreeAccess_Success() {
        // Given
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(streamAccessRepository.existsByTournamentIdAndUserId(tournamentId, userId)).thenReturn(false);
        when(streamAccessRepository.countFreeAccessByUserId(userId)).thenReturn(0);
        when(streamAccessRepository.save(any(StreamAccess.class))).thenAnswer(invocation -> {
            StreamAccess access = invocation.getArgument(0);
            access.setId(UUID.randomUUID());
            return access;
        });

        // When
        StreamAccessResponseDto result = streamAccessService.requestAccess(tournamentId, userId, freeAccessRequest);

        // Then
        assertNotNull(result);
        assertEquals("FREE", result.getAccessType());
        assertEquals(tournamentId, result.getTournamentId());
        assertEquals(userId, result.getUserId());
        verify(streamAccessRepository).save(any(StreamAccess.class));
    }

    @Test
    void requestAccess_FreeAccess_AlreadyUsedFreeAccess_ThrowsException() {
        // Given
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(streamAccessRepository.existsByTournamentIdAndUserId(tournamentId, userId)).thenReturn(false);
        when(streamAccessRepository.countFreeAccessByUserId(userId)).thenReturn(1);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> streamAccessService.requestAccess(tournamentId, userId, freeAccessRequest));
        
        assertEquals("El usuario ya ha utilizado su acceso gratuito", exception.getMessage());
        verify(streamAccessRepository, never()).save(any());
    }

    @Test
    void requestAccess_PaidAccess_Success() {
        // Given
        Ticket ticket = new Ticket();
        ticket.setId(UUID.randomUUID());
        ticket.setTournamentId(tournamentId);
        ticket.setUserId(userId);
        ticket.setStatus(Ticket.TicketStatus.ISSUED);
        
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(streamAccessRepository.existsByTournamentIdAndUserId(tournamentId, userId)).thenReturn(false);
        when(ticketRepository.findByAccessCode("TKT-12345678")).thenReturn(Optional.of(ticket));
        when(streamAccessRepository.save(any(StreamAccess.class))).thenAnswer(invocation -> {
            StreamAccess access = invocation.getArgument(0);
            access.setId(UUID.randomUUID());
            return access;
        });

        // When
        StreamAccessResponseDto result = streamAccessService.requestAccess(tournamentId, userId, paidAccessRequest);

        // Then
        assertNotNull(result);
        assertEquals("PAID", result.getAccessType());
        assertEquals(ticket.getId(), result.getTicketId());
        verify(streamAccessRepository).save(any(StreamAccess.class));
    }

    @Test
    void requestAccess_PaidAccess_InvalidTicket_ThrowsException() {
        // Given
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(streamAccessRepository.existsByTournamentIdAndUserId(tournamentId, userId)).thenReturn(false);
        when(ticketRepository.findByAccessCode("TKT-12345678")).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> streamAccessService.requestAccess(tournamentId, userId, paidAccessRequest));
        
        assertEquals("Código de ticket inválido", exception.getMessage());
        verify(streamAccessRepository, never()).save(any());
    }

    @Test
    void requestAccess_AlreadyHasAccess_ThrowsException() {
        // Given
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(streamAccessRepository.existsByTournamentIdAndUserId(tournamentId, userId)).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> streamAccessService.requestAccess(tournamentId, userId, freeAccessRequest));
        
        assertEquals("El usuario ya tiene acceso a este torneo", exception.getMessage());
        verify(streamAccessRepository, never()).save(any());
    }

    @Test
    void requestAccess_TournamentNotFound_ThrowsException() {
        // Given
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> streamAccessService.requestAccess(tournamentId, userId, freeAccessRequest));
        
        assertEquals("Torneo no encontrado", exception.getMessage());
        verify(streamAccessRepository, never()).save(any());
    }
}