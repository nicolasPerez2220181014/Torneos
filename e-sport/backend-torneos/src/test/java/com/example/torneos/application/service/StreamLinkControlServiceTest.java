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
class StreamLinkControlServiceTest {

    @Mock
    private StreamLinkControlRepository streamLinkControlRepository;

    @Mock
    private StreamAccessRepository streamAccessRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private StreamLinkControlService streamLinkControlService;

    private UUID tournamentId;
    private UUID organizerId;
    private UUID userId;
    private Tournament tournament;

    @BeforeEach
    void setUp() {
        tournamentId = UUID.randomUUID();
        organizerId = UUID.randomUUID();
        userId = UUID.randomUUID();
        
        tournament = new Tournament();
        tournament.setId(tournamentId);
        tournament.setOrganizerId(organizerId);
    }

    @Test
    void updateStreamUrl_Success() {
        // Given
        UpdateStreamUrlRequestDto request = new UpdateStreamUrlRequestDto();
        request.setStreamUrl("https://stream.example.com/tournament123");
        
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(streamLinkControlRepository.findByTournamentId(tournamentId)).thenReturn(Optional.empty());
        when(streamLinkControlRepository.save(any(StreamLinkControl.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        streamLinkControlService.updateStreamUrl(tournamentId, organizerId, request);

        // Then
        verify(streamLinkControlRepository).save(any(StreamLinkControl.class));
    }

    @Test
    void updateStreamUrl_NotOrganizer_ThrowsException() {
        // Given
        UUID otherUserId = UUID.randomUUID();
        UpdateStreamUrlRequestDto request = new UpdateStreamUrlRequestDto();
        request.setStreamUrl("https://stream.example.com/tournament123");
        
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> streamLinkControlService.updateStreamUrl(tournamentId, otherUserId, request));
        
        assertEquals("Solo el organizador puede actualizar la URL del stream", exception.getMessage());
        verify(streamLinkControlRepository, never()).save(any());
    }

    @Test
    void blockStream_Success() {
        // Given
        BlockStreamRequestDto request = new BlockStreamRequestDto();
        request.setBlockReason("Contenido inapropiado");
        
        StreamLinkControl control = new StreamLinkControl(tournamentId, "https://stream.example.com");
        
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(streamLinkControlRepository.findByTournamentId(tournamentId)).thenReturn(Optional.of(control));
        when(streamLinkControlRepository.save(any(StreamLinkControl.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        streamLinkControlService.blockStream(tournamentId, organizerId, request);

        // Then
        assertTrue(control.isBlocked());
        assertEquals("Contenido inapropiado", control.getBlockReason());
        assertNotNull(control.getBlockedAt());
        verify(streamLinkControlRepository).save(control);
    }

    @Test
    void unblockStream_Success() {
        // Given
        StreamLinkControl control = new StreamLinkControl(tournamentId, "https://stream.example.com");
        control.block("Test reason");
        
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(streamLinkControlRepository.findByTournamentId(tournamentId)).thenReturn(Optional.of(control));
        when(streamLinkControlRepository.save(any(StreamLinkControl.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        streamLinkControlService.unblockStream(tournamentId, organizerId);

        // Then
        assertFalse(control.isBlocked());
        assertNull(control.getBlockReason());
        assertNull(control.getBlockedAt());
        verify(streamLinkControlRepository).save(control);
    }

    @Test
    void getStreamStatus_WithAccess_Success() {
        // Given
        StreamLinkControl control = new StreamLinkControl(tournamentId, "https://stream.example.com");
        StreamAccess access = new StreamAccess(tournamentId, userId, StreamAccess.AccessType.FREE, null);
        
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(streamLinkControlRepository.findByTournamentId(tournamentId)).thenReturn(Optional.of(control));
        when(streamAccessRepository.findByTournamentIdAndUserId(tournamentId, userId)).thenReturn(Optional.of(access));

        // When
        StreamStatusResponseDto result = streamLinkControlService.getStreamStatus(tournamentId, userId);

        // Then
        assertNotNull(result);
        assertEquals(tournamentId, result.getTournamentId());
        assertEquals("https://stream.example.com", result.getStreamUrl());
        assertFalse(result.isBlocked());
        assertTrue(result.isHasAccess());
        assertEquals("FREE", result.getAccessType());
    }

    @Test
    void getStreamStatus_BlockedStream_HidesUrl() {
        // Given
        StreamLinkControl control = new StreamLinkControl(tournamentId, "https://stream.example.com");
        control.block("Stream blocked");
        
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));
        when(streamLinkControlRepository.findByTournamentId(tournamentId)).thenReturn(Optional.of(control));
        when(streamAccessRepository.findByTournamentIdAndUserId(tournamentId, userId)).thenReturn(Optional.empty());

        // When
        StreamStatusResponseDto result = streamLinkControlService.getStreamStatus(tournamentId, userId);

        // Then
        assertNotNull(result);
        assertTrue(result.isBlocked());
        assertEquals("Stream blocked", result.getBlockReason());
        assertNull(result.getStreamUrl()); // URL should be hidden when blocked
        assertFalse(result.isHasAccess());
    }
}