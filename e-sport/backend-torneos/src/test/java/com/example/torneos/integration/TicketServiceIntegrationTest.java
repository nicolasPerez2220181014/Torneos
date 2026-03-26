package com.example.torneos.integration;

import com.example.torneos.application.dto.response.TicketResponse;
import com.example.torneos.application.service.TicketService;
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
class TicketServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private GameTypeRepository gameTypeRepository;

    @Autowired
    private TicketOrderRepository ticketOrderRepository;

    private Tournament tournament;
    private User user;
    private TicketOrder order;

    @BeforeEach
    void setUp() {
        user = new User("user@test.com", "User", User.UserRole.USER);
        user = userRepository.save(user);

        Category category = new Category("Category");
        category = categoryRepository.save(category);

        GameType gameType = new GameType("Game");
        gameType = gameTypeRepository.save(gameType);

        tournament = new Tournament(UUID.randomUUID(), category.getId(), gameType.getId(), 
            "Tournament", "Description", true, null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        tournament = tournamentRepository.save(tournament);

        order = new TicketOrder(tournament.getId(), user.getId(), UUID.randomUUID(), 1, new java.math.BigDecimal("100.0"));
        order = ticketOrderRepository.save(order);
    }

    @Test
    void shouldCreateTicketWithUniqueAccessCode() {
        String accessCode1 = "UNIQUE-CODE-001";
        String accessCode2 = "UNIQUE-CODE-002";

        Ticket ticket1 = new Ticket(order.getId(), tournament.getId(), user.getId(), accessCode1);
        ticketRepository.save(ticket1);

        Ticket ticket2 = new Ticket(order.getId(), tournament.getId(), user.getId(), accessCode2);
        ticketRepository.save(ticket2);

        TicketResponse response1 = ticketService.findByAccessCode(accessCode1);
        TicketResponse response2 = ticketService.findByAccessCode(accessCode2);

        assertNotEquals(response1.accessCode(), response2.accessCode());
    }

    @Test
    void shouldValidateTicketOnlyOnce() {
        String accessCode = "VALIDATE-ONCE-123";
        Ticket ticket = new Ticket(order.getId(), tournament.getId(), user.getId(), accessCode);
        ticket.setStatus(Ticket.TicketStatus.ISSUED);
        ticketRepository.save(ticket);

        TicketResponse validated = ticketService.validateTicket(accessCode);
        assertEquals(TicketResponse.TicketStatus.USED, validated.status());
        assertNotNull(validated.usedAt());

        assertThrows(IllegalArgumentException.class, 
            () -> ticketService.validateTicket(accessCode));
    }

    @Test
    void shouldNotValidateCancelledTicket() {
        String accessCode = "CANCELLED-TICKET-456";
        Ticket ticket = new Ticket(order.getId(), tournament.getId(), user.getId(), accessCode);
        ticket.setStatus(Ticket.TicketStatus.CANCELLED);
        ticketRepository.save(ticket);

        assertThrows(IllegalArgumentException.class, 
            () -> ticketService.validateTicket(accessCode));
    }
}
