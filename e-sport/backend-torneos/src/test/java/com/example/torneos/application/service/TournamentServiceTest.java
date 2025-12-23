package com.example.torneos.application.service;

import com.example.torneos.application.dto.request.CreateTournamentRequest;
import com.example.torneos.application.dto.response.TournamentResponse;
import com.example.torneos.domain.model.Category;
import com.example.torneos.domain.model.GameType;
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
    private Category category;
    private GameType gameType;
    private CreateTournamentRequest validRequest;

    @BeforeEach
    void setUp() {
        organizerId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
        gameTypeId = UUID.randomUUID();
        
        organizer = new User("organizer@test.com", "Test Organizer", User.UserRole.ORGANIZER);
        organizer.setId(organizerId);
        
        category = new Category(categoryId, "Test Category", true);
        gameType = new GameType(gameTypeId, "Test Game", true);
        
        validRequest = new CreateTournamentRequest(
            categoryId.toString(),
            gameTypeId.toString(),
            "Test Tournament",
            "Test Description",
            false,
            100,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );
    }

    @Test
    void create_ShouldCreateTournament_WhenValidRequest() {
        // Given
        when(userRepository.findById(organizerId)).thenReturn(Optional.of(organizer));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(gameTypeRepository.findById(gameTypeId)).thenReturn(Optional.of(gameType));
        when(tournamentRepository.countByOrganizerIdAndIsPaidAndStatus(organizerId, false, Tournament.TournamentStatus.PUBLISHED))
            .thenReturn(0L);
        
        Tournament savedTournament = new Tournament(organizerId, categoryId, gameTypeId, 
            "Test Tournament", "Test Description", false, 100,
            validRequest.startDateTime(), validRequest.endDateTime());
        savedTournament.setId(UUID.randomUUID());
        savedTournament.setCreatedAt(LocalDateTime.now());
        savedTournament.setUpdatedAt(LocalDateTime.now());
        
        when(tournamentRepository.save(any(Tournament.class))).thenReturn(savedTournament);

        // When
        TournamentResponse response = tournamentService.create(validRequest, organizerId);

        // Then
        assertNotNull(response);
        assertEquals("Test Tournament", response.name());
        assertEquals(organizerId, response.organizerId());
        assertFalse(response.isPaid());
        verify(tournamentRepository).save(any(Tournament.class));
    }

    @Test
    void create_ShouldThrowException_WhenUserNotOrganizer() {
        // Given
        User regularUser = new User("user@test.com", "Regular User", User.UserRole.USER);
        regularUser.setId(organizerId);
        when(userRepository.findById(organizerId)).thenReturn(Optional.of(regularUser));

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> tournamentService.create(validRequest, organizerId)
        );
        assertEquals("Solo los usuarios con rol ORGANIZER pueden crear torneos", exception.getMessage());
        verify(tournamentRepository, never()).save(any(Tournament.class));
    }

    @Test
    void create_ShouldThrowException_WhenMaxFreeTournamentsReached() {
        // Given
        when(userRepository.findById(organizerId)).thenReturn(Optional.of(organizer));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(gameTypeRepository.findById(gameTypeId)).thenReturn(Optional.of(gameType));
        when(tournamentRepository.countByOrganizerIdAndIsPaidAndStatus(organizerId, false, Tournament.TournamentStatus.PUBLISHED))
            .thenReturn(2L);

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> tournamentService.create(validRequest, organizerId)
        );
        assertEquals("Un organizador solo puede tener máximo 2 torneos gratuitos activos", exception.getMessage());
        verify(tournamentRepository, never()).save(any(Tournament.class));
    }

    @Test
    void create_ShouldThrowException_WhenEndDateBeforeStartDate() {
        // Given
        CreateTournamentRequest invalidRequest = new CreateTournamentRequest(
            categoryId.toString(), gameTypeId.toString(), "Test Tournament", "Test Description", false, 100,
            LocalDateTime.now().plusDays(2), // end before start
            LocalDateTime.now().plusDays(1)
        );
        
        when(userRepository.findById(organizerId)).thenReturn(Optional.of(organizer));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(gameTypeRepository.findById(gameTypeId)).thenReturn(Optional.of(gameType));

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> tournamentService.create(invalidRequest, organizerId)
        );
        assertEquals("La fecha de fin debe ser posterior a la fecha de inicio", exception.getMessage());
        verify(tournamentRepository, never()).save(any(Tournament.class));
    }

    @Test
    void create_ShouldThrowException_WhenFreeTournamentWithoutCapacity() {
        // Given
        CreateTournamentRequest requestWithoutCapacity = new CreateTournamentRequest(
            categoryId.toString(), gameTypeId.toString(), "Test Tournament", "Test Description", false, null,
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)
        );
        
        when(userRepository.findById(organizerId)).thenReturn(Optional.of(organizer));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(gameTypeRepository.findById(gameTypeId)).thenReturn(Optional.of(gameType));

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> tournamentService.create(requestWithoutCapacity, organizerId)
        );
        assertEquals("Los torneos gratuitos deben tener una capacidad máxima definida", exception.getMessage());
        verify(tournamentRepository, never()).save(any(Tournament.class));
    }
}