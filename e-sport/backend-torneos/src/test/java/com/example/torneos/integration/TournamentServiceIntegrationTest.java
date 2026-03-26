package com.example.torneos.integration;

import com.example.torneos.application.dto.request.CreateTournamentRequest;
import com.example.torneos.application.dto.response.TournamentResponse;
import com.example.torneos.application.service.TournamentService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class TournamentServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private GameTypeRepository gameTypeRepository;

    private User organizer;
    private Category category;
    private GameType gameType;

    @BeforeEach
    void setUp() {
        organizer = new User("organizer@test.com", "Organizer", User.UserRole.ORGANIZER);
        organizer = userRepository.save(organizer);

        category = new Category("Category Test");
        category = categoryRepository.save(category);

        gameType = new GameType("Game Test");
        gameType = gameTypeRepository.save(gameType);
    }

    @Test
    void shouldEnforceMaxTwoFreeTournamentsRule() {
        CreateTournamentRequest request1 = new CreateTournamentRequest(
            category.getId().toString(),
            gameType.getId().toString(),
            "Free Tournament 1",
            "Description",
            false,
            100,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );

        CreateTournamentRequest request2 = new CreateTournamentRequest(
            category.getId().toString(),
            gameType.getId().toString(),
            "Free Tournament 2",
            "Description",
            false,
            100,
            LocalDateTime.now().plusDays(3),
            LocalDateTime.now().plusDays(4)
        );

        TournamentResponse tournament1 = tournamentService.create(request1, organizer.getId());
        tournamentService.publish(tournament1.id(), organizer.getId());

        TournamentResponse tournament2 = tournamentService.create(request2, organizer.getId());
        tournamentService.publish(tournament2.id(), organizer.getId());

        CreateTournamentRequest request3 = new CreateTournamentRequest(
            category.getId().toString(),
            gameType.getId().toString(),
            "Free Tournament 3",
            "Description",
            false,
            100,
            LocalDateTime.now().plusDays(5),
            LocalDateTime.now().plusDays(6)
        );

        assertThrows(IllegalArgumentException.class, 
            () -> tournamentService.create(request3, organizer.getId()));
    }

    @Test
    void shouldAllowPaidTournamentsWithoutLimit() {
        CreateTournamentRequest request1 = new CreateTournamentRequest(
            category.getId().toString(),
            gameType.getId().toString(),
            "Paid Tournament 1",
            "Description",
            true,
            null,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );

        CreateTournamentRequest request2 = new CreateTournamentRequest(
            category.getId().toString(),
            gameType.getId().toString(),
            "Paid Tournament 2",
            "Description",
            true,
            null,
            LocalDateTime.now().plusDays(3),
            LocalDateTime.now().plusDays(4)
        );

        CreateTournamentRequest request3 = new CreateTournamentRequest(
            category.getId().toString(),
            gameType.getId().toString(),
            "Paid Tournament 3",
            "Description",
            true,
            null,
            LocalDateTime.now().plusDays(5),
            LocalDateTime.now().plusDays(6)
        );

        assertDoesNotThrow(() -> {
            tournamentService.create(request1, organizer.getId());
            tournamentService.create(request2, organizer.getId());
            tournamentService.create(request3, organizer.getId());
        });
    }

    @Test
    void shouldOnlyPublishDraftTournaments() {
        CreateTournamentRequest request = new CreateTournamentRequest(
            category.getId().toString(),
            gameType.getId().toString(),
            "Tournament",
            "Description",
            true,
            null,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2)
        );

        TournamentResponse tournament = tournamentService.create(request, organizer.getId());
        assertEquals(TournamentResponse.TournamentStatus.DRAFT, tournament.status());

        tournamentService.publish(tournament.id(), organizer.getId());
        TournamentResponse published = tournamentService.findById(tournament.id());
        assertEquals(TournamentResponse.TournamentStatus.PUBLISHED, published.status());

        assertThrows(IllegalArgumentException.class, 
            () -> tournamentService.publish(tournament.id(), organizer.getId()));
    }
}
