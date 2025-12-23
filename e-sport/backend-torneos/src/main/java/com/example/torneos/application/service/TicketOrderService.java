package com.example.torneos.application.service;

import com.example.torneos.application.dto.request.CreateTicketOrderRequest;
import com.example.torneos.application.dto.response.TicketOrderResponse;
import com.example.torneos.domain.model.*;
import com.example.torneos.domain.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
public class TicketOrderService {

    private final TicketOrderRepository orderRepository;
    private final TicketSaleStageRepository stageRepository;
    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final AuditLogService auditLogService;
    private final Random random = new Random();

    public TicketOrderService(TicketOrderRepository orderRepository,
                            TicketSaleStageRepository stageRepository,
                            TournamentRepository tournamentRepository,
                            UserRepository userRepository,
                            TicketRepository ticketRepository,
                            AuditLogService auditLogService) {
        this.orderRepository = orderRepository;
        this.stageRepository = stageRepository;
        this.tournamentRepository = tournamentRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TicketOrderResponse createOrder(UUID tournamentId, CreateTicketOrderRequest request, UUID userId) {
        // Validar que el usuario existe
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Validar que el torneo existe
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado"));

        // 1. Lock stage for update to prevent race conditions
        TicketSaleStage stage = stageRepository.findByIdForUpdate(request.stageId())
                .orElseThrow(() -> new IllegalArgumentException("Etapa de venta no encontrada"));

        // Validar que la etapa pertenece al torneo
        if (!stage.getTournamentId().equals(tournamentId)) {
            throw new IllegalArgumentException("La etapa no pertenece a este torneo");
        }

        // Validar que la etapa está activa
        if (!stage.isActive()) {
            throw new IllegalArgumentException("La etapa de venta no está activa");
        }

        // Validar fechas de la etapa
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(stage.getStartDateTime())) {
            throw new IllegalArgumentException("La etapa de venta aún no ha comenzado");
        }
        if (now.isAfter(stage.getEndDateTime())) {
            throw new IllegalArgumentException("La etapa de venta ha finalizado");
        }

        // 2. Check capacity atomically using domain method
        if (!stage.hasAvailableCapacity(request.quantity())) {
            throw new IllegalArgumentException("No hay suficiente capacidad disponible en esta etapa");
        }

        // Validar capacidad real contra tickets ya vendidos
        long ticketsVendidos = orderRepository.countByStageIdAndStatus(stage.getId(), TicketOrder.OrderStatus.APPROVED);
        if (ticketsVendidos + request.quantity() > stage.getCapacity()) {
            throw new IllegalArgumentException("No hay suficiente capacidad disponible en esta etapa");
        }

        // Calcular monto total
        BigDecimal totalAmount = stage.getPrice().multiply(BigDecimal.valueOf(request.quantity()));

        // 3. Create order
        TicketOrder order = new TicketOrder(
            tournamentId,
            userId,
            request.stageId(),
            request.quantity(),
            totalAmount
        );

        TicketOrder savedOrder = orderRepository.save(order);

        // Simular procesamiento de pago (educativo)
        simulatePaymentProcessing(savedOrder);

        return mapToResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public TicketOrderResponse findById(UUID orderId) {
        TicketOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));
        
        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public List<TicketOrderResponse> findByTournamentId(UUID tournamentId) {
        return orderRepository.findByTournamentId(tournamentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private void simulatePaymentProcessing(TicketOrder order) {
        // Simulación educativa de procesamiento de pago
        // 80% aprobado, 20% rechazado
        boolean approved = random.nextDouble() < 0.8;
        
        if (approved) {
            order.setStatus(TicketOrder.OrderStatus.APPROVED);
            orderRepository.save(order);
            
            // Generar tickets
            generateTickets(order);
            
            // Auditoría
            auditLogService.logEvent(
                com.example.torneos.domain.model.AuditLog.EventType.TICKET_PURCHASED,
                com.example.torneos.domain.model.AuditLog.EntityType.ORDER,
                order.getId(),
                order.getUserId(),
                String.format("Compra aprobada: %d tickets por $%s", order.getQuantity(), order.getTotalAmount())
            );
        } else {
            order.setStatus(TicketOrder.OrderStatus.REJECTED);
            orderRepository.save(order);
        }
    }

    private void generateTickets(TicketOrder order) {
        List<Ticket> tickets = new ArrayList<>();
        
        for (int i = 0; i < order.getQuantity(); i++) {
            String accessCode = generateUniqueAccessCode();
            Ticket ticket = new Ticket(
                order.getId(),
                order.getTournamentId(),
                order.getUserId(),
                accessCode
            );
            tickets.add(ticket);
        }
        
        // Guardar todos los tickets
        tickets.forEach(ticketRepository::save);
    }

    private String generateUniqueAccessCode() {
        String code;
        do {
            code = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (ticketRepository.existsByAccessCode(code));
        
        return code;
    }

    private TicketOrderResponse mapToResponse(TicketOrder order) {
        return new TicketOrderResponse(
            order.getId(),
            order.getTournamentId(),
            order.getUserId(),
            order.getStageId(),
            order.getQuantity(),
            order.getTotalAmount(),
            TicketOrderResponse.OrderStatus.valueOf(order.getStatus().name()),
            order.getCreatedAt()
        );
    }
}