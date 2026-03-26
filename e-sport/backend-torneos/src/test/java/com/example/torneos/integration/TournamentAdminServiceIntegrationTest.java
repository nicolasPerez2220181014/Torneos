package com.example.torneos.integration;

import com.example.torneos.application.dto.request.AssignSubAdminRequest;
import com.example.torneos.application.dto.response.TournamentAdminResponse;
import com.example.torneos.application.service.TournamentAdminService;
import com.example.torneos.domain.model.*;
import com.example.torneos.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class TournamentAdminServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TournamentAdminService adminService;

    @Autowired
    private TournamentAdminRepository adminRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private GameTypeRepository gameTypeRepository;

    private Tournament tournament;
    private User organizer;
    private User subAdmin1;
    private User subAdmin2;
    private User subAdmin3;

    @BeforeEach
    void setUp() {
        organizer = new User("organizer@test.com", "Organizer", User.UserRole.ORGANIZER);
        organizer = userRepository.save(organizer);

        subAdmin1 = new User("subadmin1@test.com", "SubAdmin1", User.UserRole.USER);
        subAdmin1 = userRepository.save(subAdmin1);

        subAdmin2 = new User("subadmin2@test.com", "SubAdmin2", User.UserRole.USER);
        subAdmin2 = userRepository.save(subAdmin2);

        subAdmin3 = new User("subadmin3@test.com", "SubAdmin3", User.UserRole.USER);
        subAdmin3 = userRepository.save(subAdmin3);

        Category category = new Category("Category");
        category = categoryRepository.save(category);

        GameType gameType = new GameType("Game");
        gameType = gameTypeRepository.save(gameType);

        tournament = new Tournament(organizer.getId(), category.getId(), gameType.getId(), 
            "Tournament", "Description", true, null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        tournament = tournamentRepository.save(tournament);
    }

    @Test
    void shouldEnforceMaxTwoSubAdminsRule() {
        AssignSubAdminRequest request1 = new AssignSubAdminRequest(subAdmin1.getId());
        AssignSubAdminRequest request2 = new AssignSubAdminRequest(subAdmin2.getId());
        AssignSubAdminRequest request3 = new AssignSubAdminRequest(subAdmin3.getId());

        adminService.assignSubAdmin(tournament.getId(), request1, organizer.getId());
        adminService.assignSubAdmin(tournament.getId(), request2, organizer.getId());

        assertThrows(IllegalArgumentException.class, 
            () -> adminService.assignSubAdmin(tournament.getId(), request3, organizer.getId()));

        List<TournamentAdminResponse> admins = adminService.findByTournamentId(tournament.getId());
        assertEquals(2, admins.size());
    }

    @Test
    void shouldAllowAddingThirdSubAdminAfterRemovingOne() {
        AssignSubAdminRequest request1 = new AssignSubAdminRequest(subAdmin1.getId());
        AssignSubAdminRequest request2 = new AssignSubAdminRequest(subAdmin2.getId());

        adminService.assignSubAdmin(tournament.getId(), request1, organizer.getId());
        adminService.assignSubAdmin(tournament.getId(), request2, organizer.getId());

        adminService.removeSubAdmin(tournament.getId(), subAdmin1.getId(), organizer.getId());

        AssignSubAdminRequest request3 = new AssignSubAdminRequest(subAdmin3.getId());
        assertDoesNotThrow(() -> adminService.assignSubAdmin(tournament.getId(), request3, organizer.getId()));

        List<TournamentAdminResponse> admins = adminService.findByTournamentId(tournament.getId());
        assertEquals(2, admins.size());
    }

    @Test
    void shouldNotAllowDuplicateSubAdmin() {
        AssignSubAdminRequest request = new AssignSubAdminRequest(subAdmin1.getId());

        adminService.assignSubAdmin(tournament.getId(), request, organizer.getId());

        assertThrows(IllegalArgumentException.class, 
            () -> adminService.assignSubAdmin(tournament.getId(), request, organizer.getId()));
    }

    @Test
    void shouldNotAllowOrganizerAsSubAdmin() {
        AssignSubAdminRequest request = new AssignSubAdminRequest(organizer.getId());

        assertThrows(IllegalArgumentException.class, 
            () -> adminService.assignSubAdmin(tournament.getId(), request, organizer.getId()));
    }
}
