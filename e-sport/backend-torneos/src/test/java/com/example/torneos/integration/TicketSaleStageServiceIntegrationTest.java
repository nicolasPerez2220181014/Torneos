package com.example.torneos.integration;

import com.example.torneos.application.dto.request.CreateTicketSaleStageRequest;
import com.example.torneos.application.dto.response.TicketSaleStageResponse;
import com.example.torneos.application.service.TicketSaleStageService;
import com.example.torneos.domain.model.*;
import com.example.torneos.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class TicketSaleStageServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TicketSaleStageService stageService;

    @Autowired
    private TicketSaleStageRepository stageRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private GameTypeRepository gameTypeRepository;

    private Tournament paidTournament;
    private User organizer;

    @BeforeEach
    void setUp() {
        organizer = new User("organizer@test.com", "Organizer", User.UserRole.ORGANIZER);
        organizer = userRepository.save(organizer);

        Category category = new Category("Category");
        category = categoryRepository.save(category);

        GameType gameType = new GameType("Game");
        gameType = gameTypeRepository.save(gameType);

        paidTournament = new Tournament(organizer.getId(), category.getId(), gameType.getId(), 
            "Paid Tournament", "Description", true, null, LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(20));
        paidTournament = tournamentRepository.save(paidTournament);
    }

    @Test
    void shouldCreateActiveStageWhenDatesAreCurrent() {
        CreateTicketSaleStageRequest request = new CreateTicketSaleStageRequest(
            CreateTicketSaleStageRequest.StageType.EARLY_BIRD,
            new BigDecimal("50.0"),
            100,
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(5)
        );

        TicketSaleStageResponse stage = stageService.create(paidTournament.getId(), request, organizer.getId());

        assertNotNull(stage);
        assertTrue(stage.active());
    }

    @Test
    void shouldNotAllowDuplicateStageTypes() {
        CreateTicketSaleStageRequest request1 = new CreateTicketSaleStageRequest(
            CreateTicketSaleStageRequest.StageType.EARLY_BIRD,
            new BigDecimal("50.0"),
            100,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(5)
        );

        stageService.create(paidTournament.getId(), request1, organizer.getId());

        CreateTicketSaleStageRequest request2 = new CreateTicketSaleStageRequest(
            CreateTicketSaleStageRequest.StageType.EARLY_BIRD,
            new BigDecimal("60.0"),
            150,
            LocalDateTime.now().plusDays(6),
            LocalDateTime.now().plusDays(10)
        );

        assertThrows(IllegalArgumentException.class, 
            () -> stageService.create(paidTournament.getId(), request2, organizer.getId()));
    }

    @Test
    void shouldOnlyAllowStagesForPaidTournaments() {
        Tournament freeTournament = new Tournament(organizer.getId(), 
            paidTournament.getCategoryId(), paidTournament.getGameTypeId(), 
            "Free Tournament", "Description", false, 100, 
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        freeTournament = tournamentRepository.save(freeTournament);

        CreateTicketSaleStageRequest request = new CreateTicketSaleStageRequest(
            CreateTicketSaleStageRequest.StageType.EARLY_BIRD,
            new BigDecimal("50.0"),
            100,
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(5)
        );

        UUID finalTournamentId = freeTournament.getId();
        assertThrows(IllegalArgumentException.class, 
            () -> stageService.create(finalTournamentId, request, organizer.getId()));
    }

    @Test
    void shouldValidateActiveStageDates() {
        CreateTicketSaleStageRequest earlyBird = new CreateTicketSaleStageRequest(
            CreateTicketSaleStageRequest.StageType.EARLY_BIRD,
            new BigDecimal("50.0"),
            100,
            LocalDateTime.now().minusDays(2),
            LocalDateTime.now().plusDays(2)
        );

        CreateTicketSaleStageRequest regular = new CreateTicketSaleStageRequest(
            CreateTicketSaleStageRequest.StageType.REGULAR,
            new BigDecimal("75.0"),
            150,
            LocalDateTime.now().plusDays(3),
            LocalDateTime.now().plusDays(7)
        );

        TicketSaleStageResponse stage1 = stageService.create(paidTournament.getId(), earlyBird, organizer.getId());
        TicketSaleStageResponse stage2 = stageService.create(paidTournament.getId(), regular, organizer.getId());

        assertTrue(stage1.active());
        assertFalse(stage2.active());

        List<TicketSaleStageResponse> stages = stageService.findByTournamentId(paidTournament.getId());
        assertEquals(2, stages.size());
    }
}
