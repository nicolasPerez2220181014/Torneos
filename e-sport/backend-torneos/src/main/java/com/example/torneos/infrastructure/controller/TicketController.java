package com.example.torneos.infrastructure.controller;

import com.example.torneos.application.dto.request.CreateTicketOrderRequest;
import com.example.torneos.application.dto.response.TicketOrderResponse;
import com.example.torneos.application.dto.response.TicketResponse;
import com.example.torneos.application.service.TicketOrderService;
import com.example.torneos.application.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Tickets & Orders", description = "API para gestión de órdenes y tickets")
public class TicketController {

    private final TicketOrderService orderService;
    private final TicketService ticketService;

    public TicketController(TicketOrderService orderService, TicketService ticketService) {
        this.orderService = orderService;
        this.ticketService = ticketService;
    }

    @PostMapping("/api/tournaments/{tournamentId}/orders")
    @Operation(summary = "Comprar tickets", description = "Crea una orden de compra de tickets")
    public ResponseEntity<TicketOrderResponse> createOrder(
            @Parameter(description = "ID del torneo") @PathVariable UUID tournamentId,
            @Valid @RequestBody CreateTicketOrderRequest request,
            @RequestHeader("X-USER-ID") String userIdHeader) {
        
        UUID userId = UUID.fromString(userIdHeader);
        TicketOrderResponse response = orderService.createOrder(tournamentId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/orders/{orderId}")
    @Operation(summary = "Obtener orden", description = "Obtiene una orden por su ID")
    public ResponseEntity<TicketOrderResponse> getOrder(
            @Parameter(description = "ID de la orden") @PathVariable UUID orderId) {
        TicketOrderResponse response = orderService.findById(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/tournaments/{tournamentId}/tickets")
    @Operation(summary = "Listar tickets de torneo", description = "Obtiene tickets de un torneo, opcionalmente filtrados por usuario")
    public ResponseEntity<List<TicketResponse>> getTickets(
            @Parameter(description = "ID del torneo") @PathVariable UUID tournamentId,
            @RequestParam(required = false) UUID userId) {
        
        List<TicketResponse> response;
        if (userId != null) {
            response = ticketService.findByTournamentIdAndUserId(tournamentId, userId);
        } else {
            response = ticketService.findByTournamentId(tournamentId);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/tickets/{accessCode}/validate")
    @Operation(summary = "Validar ticket", description = "Valida un ticket y lo marca como usado")
    public ResponseEntity<TicketResponse> validateTicket(
            @Parameter(description = "Código de acceso del ticket") @PathVariable String accessCode) {
        TicketResponse response = ticketService.validateTicket(accessCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/tickets/{accessCode}")
    @Operation(summary = "Obtener ticket por código", description = "Obtiene información de un ticket por su código de acceso")
    public ResponseEntity<TicketResponse> getTicketByAccessCode(
            @Parameter(description = "Código de acceso del ticket") @PathVariable String accessCode) {
        TicketResponse response = ticketService.findByAccessCode(accessCode);
        return ResponseEntity.ok(response);
    }
}