package com.example.torneos.integration;

import com.example.torneos.application.dto.request.StreamAccessRequestDto;
import com.example.torneos.application.dto.response.StreamAccessResponseDto;
import com.example.torneos.application.service.StreamAccessService;
import com.example.torneos.domain.model.*;
import com.example.torneos.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class StreamAccessServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private StreamAccessService streamAccessService;

    @Autowired
    private StreamAccessRepository streamAccessRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private GameTypeRepository gameTypeRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketOrderRepository ticketOrderRepository;

    private Tournament freeTournament;
    private Tournament paidTournament;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User("user@test.com", "User", User.UserRole.USER);
        user = userRepository.save(user);

        Category category = new Category("Category");
        category = categoryRepository.save(category);

        GameType gameType = new GameType("Game");
        gameType = gameTypeRepository.save(gameType);

        freeTournament = new Tournament(UUID.randomUUID(), category.getId(), gameType.getId(), 
            "Free Tournament", "Description", false, 100, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        freeTournament = tournamentRepository.save(freeTournament);

        paidTournament = new Tournament(UUID.randomUUID(), category.getId(), gameType.getId(), 
            "Paid Tournament", "Description", true, null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        paidTournament = tournamentRepository.save(paidTournament);
    }

    @Test
    void shouldAllowOnlyOneFreeAccessPerUser() {
        StreamAccessRequestDto request1 = new StreamAccessRequestDto();
        request1.setAccessType("FREE");

        StreamAccessResponseDto access1 = streamAccessService.requestAccess(freeTournament.getId(), user.getId(), request1);
        assertNotNull(access1);
        assertEquals("FREE", access1.getAccessType());

        Tournament anotherFreeTournament = new Tournament(UUID.randomUUID(), 
            freeTournament.getCategoryId(), freeTournament.getGameTypeId(), 
            "Another Free Tournament", "Description", false, 100, 
            LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4));
        anotherFreeTournament = tournamentRepository.save(anotherFreeTournament);

        StreamAccessRequestDto request2 = new StreamAccessRequestDto();
        request2.setAccessType("FREE");

        UUID finalTournamentId = anotherFreeTournament.getId();
        assertThrows(IllegalArgumentException.class, 
            () -> streamAccessService.requestAccess(finalTournamentId, user.getId(), request2));
    }

    @Test
    void shouldRequireValidTicketForPaidAccess() {
        TicketOrder order = new TicketOrder(paidTournament.getId(), user.getId(), UUID.randomUUID(), 1, new BigDecimal("100.0"));
        order = ticketOrderRepository.save(order);

        String ticketCode = "PAID-TICKET-123";
        Ticket ticket = new Ticket(order.getId(), paidTournament.getId(), user.getId(), ticketCode);
        ticket.setStatus(Ticket.TicketStatus.ISSUED);
        ticketRepository.save(ticket);

        StreamAccessRequestDto request = new StreamAccessRequestDto();
        request.setAccessType("PAID");
        request.setTicketAccessCode(ticketCode);

        StreamAccessResponseDto access = streamAccessService.requestAccess(paidTournament.getId(), user.getId(), request);
        assertNotNull(access);
        assertEquals("PAID", access.getAccessType());
    }

    @Test
    void shouldNotAllowDuplicateAccessToSameTournament() {
        StreamAccessRequestDto request = new StreamAccessRequestDto();
        request.setAccessType("FREE");

        streamAccessService.requestAccess(freeTournament.getId(), user.getId(), request);

        assertThrows(IllegalArgumentException.class, 
            () -> streamAccessService.requestAccess(freeTournament.getId(), user.getId(), request));
    }

    @Test
    void shouldRejectPaidAccessWithoutTicket() {
        StreamAccessRequestDto request = new StreamAccessRequestDto();
        request.setAccessType("PAID");

        assertThrows(IllegalArgumentException.class, 
            () -> streamAccessService.requestAccess(paidTournament.getId(), user.getId(), request));
    }
}
