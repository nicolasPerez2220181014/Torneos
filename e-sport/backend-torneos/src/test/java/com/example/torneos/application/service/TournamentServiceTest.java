package com.example.torneos.application.service;

import com.example.torneos.application.dto.request.CreateTournamentRequest;
import com.example.torneos.application.dto.response.TournamentResponse;
import com.example.torneos.domain.model.Tournament;
import com.example.torneos.domain.model.User;
import com.example.torneos.domain.repository.CategoryRepository;
import com.example.torneos.domain.repository.GameTypeRepository;
import com.example.torneos.domain.repository.TournamentRepository;
import com.example.torneos.domain.repository.UserRepository;
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
class TournamentServiceTest {

    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private GameTypeRepository gameTypeRepository;
    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private TournamentService tournamentService;

    private UUID organizerId;
    private UUID categoryId;
    private UUID gameTypeId;
    private User organizer;

    @BeforeEach
    void setUp() {
        organizerId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        gameTypeId = UUID.randomUUID();
        
        organizer = new User("organizer@test.com", "Organizer", User.UserRole.ORGANIZER);
    }

    @Test
    void create_shouldThrowException_whenMaxFreeTournamentsReached() {
        CreateTournamentRequest request = new CreateTournamentRequest(
            categoryId.toString(),
            gameTypeId.toString(),
            "Free Tournament",
            "Description",
            false,
            100,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );

        when(userRepository.findById(organizerId)).thenReturn(Optional.of(organizer));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mock(com.example.torneos.domain.model.Category.class)));
        when(gameTypeRepository.findById(gameTypeId)).thenReturn(Optional.of(mock(com.example.torneos.domain.model.GameType.class)));
        when(tournamentRepository.countByOrganizerIdAndIsPaidAndStatus(organizerId, false, Tournament.TournamentStatus.PUBLISHED))
            .thenReturn(2L);

        assertThrows(IllegalArgumentException.class, () -> tournamentService.create(request, organizerId));
        verify(tournamentRepository, never()).save(any());
    }

    @Test
    void create_shouldCreateFreeTournament_whenLessThanMaxFree() {
        CreateTournamentRequest request = new CreateTournamentRequest(
            categoryId.toString(),
            gameTypeId.toString(),
            "Free Tournament",
            "Description",
            false,
            100,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );

        Tournament tournament = new Tournament(organizerId, categoryId, gameTypeId, "Free Tournament", "Description", false, 100, request.startDateTime(), request.endDateTime());

        when(userRepository.findById(organizerId)).thenReturn(Optional.of(organizer));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mock(com.example.torneos.domain.model.Category.class)));
        when(gameTypeRepository.findById(gameTypeId)).thenReturn(Optional.of(mock(com.example.torneos.domain.model.GameType.class)));
        when(tournamentRepository.countByOrganizerIdAndIsPaidAndStatus(organizerId, false, Tournament.TournamentStatus.PUBLISHED))
            .thenReturn(1L);
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

        TournamentResponse response = tournamentService.create(request, organizerId);

        assertNotNull(response);
        verify(tournamentRepository).save(any(Tournament.class));
        verify(auditLogService).logEvent(any(), any(), any(), any(), any());
    }

    @Test
    void create_shouldThrowException_whenOrganizerNotFound() {
        CreateTournamentRequest request = new CreateTournamentRequest(
            categoryId.toString(),
            gameTypeId.toString(),
            "Tournament",
            "Description",
            true,
            null,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );

        when(userRepository.findById(organizerId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> tournamentService.create(request, organizerId));
    }

    @Test
    void publish_shouldThrowException_whenNotInDraftStatus() {
        UUID tournamentId = UUID.randomUUID();
        Tournament tournament = new Tournament(organizerId, categoryId, gameTypeId, "Tournament", "Description", true, null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        tournament.setStatus(Tournament.TournamentStatus.PUBLISHED);

        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        assertThrows(IllegalArgumentException.class, () -> tournamentService.publish(tournamentId, organizerId));
    }
}
