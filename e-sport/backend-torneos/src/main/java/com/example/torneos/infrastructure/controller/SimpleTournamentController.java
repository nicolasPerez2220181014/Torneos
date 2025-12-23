package com.example.torneos.infrastructure.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/simple")
@CrossOrigin(origins = "http://localhost:4200")
public class SimpleTournamentController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/tournaments")
    public ResponseEntity<Map<String, Object>> createTournament(@RequestBody Map<String, Object> request) {
        try {
            UUID tournamentId = UUID.randomUUID();
            String organizerId = "123e4567-e89b-12d3-a456-426614174000"; // Usuario fijo
            
            String sql = """
                INSERT INTO tournaments (id, organizer_id, category_id, game_type_id, name, description, 
                                       is_paid, max_free_capacity, start_date_time, end_date_time, status) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'DRAFT')
                """;
            
            jdbcTemplate.update(sql,
                tournamentId,
                UUID.fromString(organizerId),
                UUID.fromString((String) request.get("categoryId")),
                UUID.fromString((String) request.get("gameTypeId")),
                request.get("name"),
                request.get("description"),
                request.get("isPaid"),
                request.get("maxFreeCapacity"),
                LocalDateTime.parse((String) request.get("startDateTime")),
                LocalDateTime.parse((String) request.get("endDateTime"))
            );
            
            return ResponseEntity.ok(Map.of(
                "id", tournamentId.toString(),
                "name", request.get("name"),
                "status", "DRAFT",
                "message", "Torneo creado exitosamente"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error creando torneo: " + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/tournaments/{id}")
    public ResponseEntity<Map<String, Object>> getTournament(@PathVariable String id) {
        try {
            String sql = """
                SELECT t.id, t.name, t.description, t.status, t.is_paid, t.max_free_capacity,
                       t.start_date_time, t.end_date_time, t.organizer_id, t.category_id, t.game_type_id,
                       c.name as category_name, g.name as game_type_name
                FROM tournaments t
                LEFT JOIN categories c ON t.category_id = c.id
                LEFT JOIN game_types g ON t.game_type_id = g.id
                WHERE t.id = ?
                """;
            
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, UUID.fromString(id));
            
            if (results.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(results.get(0));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error obteniendo torneo: " + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/tournaments")
    public ResponseEntity<Map<String, Object>> getTournaments() {
        try {
            String sql = """
                SELECT t.id, t.name, t.description, t.status, t.is_paid, t.max_free_capacity,
                       t.start_date_time, t.end_date_time, t.organizer_id, t.category_id, t.game_type_id,
                       c.name as category_name, g.name as game_type_name
                FROM tournaments t
                LEFT JOIN categories c ON t.category_id = c.id
                LEFT JOIN game_types g ON t.game_type_id = g.id
                ORDER BY t.start_date_time DESC
                """;
            
            List<Map<String, Object>> tournaments = jdbcTemplate.queryForList(sql);
            
            return ResponseEntity.ok(Map.of(
                "content", tournaments,
                "pageable", Map.of(
                    "totalElements", tournaments.size(),
                    "totalPages", 1,
                    "pageSize", tournaments.size(),
                    "pageNumber", 0
                )
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error obteniendo torneos: " + e.getMessage()
            ));
        }
    }
    
    @GetMapping("/dashboard/metrics")
    public ResponseEntity<Map<String, Object>> getDashboardMetrics() {
        try {
            // Obtener métricas reales de la base de datos
            String tournamentsSql = "SELECT COUNT(*) as total, COUNT(CASE WHEN status = 'PUBLISHED' THEN 1 END) as active FROM tournaments";
            String usersSql = "SELECT COUNT(*) FROM users";
            String ticketsSql = "SELECT COUNT(*) as total_tickets, COALESCE(SUM(total_amount), 0) as total_revenue FROM ticket_orders WHERE status = 'APPROVED'";
            
            Map<String, Object> tournamentMetrics = jdbcTemplate.queryForMap(tournamentsSql);
            Long totalUsers = jdbcTemplate.queryForObject(usersSql, Long.class);
            
            // Obtener métricas de tickets
            Map<String, Object> ticketMetrics;
            try {
                ticketMetrics = jdbcTemplate.queryForMap(ticketsSql);
            } catch (Exception e) {
                // Si no hay datos de tickets, usar valores por defecto
                ticketMetrics = Map.of("total_tickets", 0L, "total_revenue", 0.0);
            }
            
            Map<String, Object> metrics = Map.of(
                "totalTournaments", tournamentMetrics.get("total"),
                "activeTournaments", tournamentMetrics.get("active"),
                "totalUsers", totalUsers,
                "totalTicketsSold", ticketMetrics.get("total_tickets"),
                "totalRevenue", ticketMetrics.get("total_revenue"),
                "activeStreams", 0,
                "totalViews", 0,
                "recentActivity", List.of(
                    Map.of("type", "TOURNAMENTS_CREATED", "count", tournamentMetrics.get("total"), "label", "Torneos Creados", "trend", 0),
                    Map.of("type", "USERS_REGISTERED", "count", totalUsers, "label", "Usuarios Registrados", "trend", 0),
                    Map.of("type", "TICKETS_SOLD", "count", ticketMetrics.get("total_tickets"), "label", "Tickets Vendidos", "trend", 0)
                )
            );
            
            return ResponseEntity.ok(metrics);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error obteniendo métricas: " + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/tickets/purchase")
    public ResponseEntity<Map<String, Object>> purchaseTickets(@RequestBody Map<String, Object> request) {
        try {
            UUID orderId = UUID.randomUUID();
            String userId = "770e8400-e29b-41d4-a716-446655440001";
            
            // Crear la orden
            String orderSql = """
                INSERT INTO ticket_orders (id, tournament_id, user_id, quantity, total_amount, status, created_at) 
                VALUES (?, ?, ?, ?, ?, 'APPROVED', NOW())
                """;
            
            jdbcTemplate.update(orderSql,
                orderId,
                UUID.fromString((String) request.get("tournamentId")),
                UUID.fromString(userId),
                request.get("quantity"),
                request.get("totalAmount")
            );
            
            // Crear tickets individuales con códigos de acceso
            String ticketSql = """
                INSERT INTO tickets (id, order_id, tournament_id, user_id, access_code, status, created_at) 
                VALUES (?, ?, ?, ?, ?, 'ISSUED', NOW())
                """;
            
            int quantity = (Integer) request.get("quantity");
            List<String> accessCodes = new java.util.ArrayList<>();
            
            for (int i = 0; i < quantity; i++) {
                UUID ticketId = UUID.randomUUID();
                String accessCode = generateAccessCode();
                accessCodes.add(accessCode);
                
                jdbcTemplate.update(ticketSql,
                    ticketId,
                    orderId,
                    UUID.fromString((String) request.get("tournamentId")),
                    UUID.fromString(userId),
                    accessCode
                );
            }
            
            return ResponseEntity.ok(Map.of(
                "id", orderId.toString(),
                "accessCodes", accessCodes,
                "message", "Compra realizada exitosamente"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error procesando compra: " + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/tickets/validate")
    public ResponseEntity<Map<String, Object>> validateTicket(@RequestBody Map<String, Object> request) {
        try {
            String accessCode = (String) request.get("accessCode");
            
            String sql = """
                SELECT t.id, t.access_code, t.status, t.used_at, 
                       tor.tournament_id, tr.name as tournament_name,
                       u.full_name as user_name
                FROM tickets t
                JOIN ticket_orders tor ON t.order_id = tor.id
                JOIN tournaments tr ON tor.tournament_id = tr.id
                JOIN users u ON t.user_id = u.id
                WHERE t.access_code = ?
                """;
            
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, accessCode);
            
            if (results.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "valid", false,
                    "message", "Código de acceso no encontrado"
                ));
            }
            
            Map<String, Object> ticket = results.get(0);
            String status = (String) ticket.get("status");
            
            if ("USED".equals(status)) {
                return ResponseEntity.ok(Map.of(
                    "valid", false,
                    "message", "Este ticket ya fue utilizado",
                    "ticket", ticket
                ));
            }
            
            if ("CANCELLED".equals(status)) {
                return ResponseEntity.ok(Map.of(
                    "valid", false,
                    "message", "Este ticket fue cancelado",
                    "ticket", ticket
                ));
            }
            
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "message", "Ticket válido",
                "ticket", ticket
            ));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error validando ticket: " + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/tickets/use")
    public ResponseEntity<Map<String, Object>> useTicket(@RequestBody Map<String, Object> request) {
        try {
            String accessCode = (String) request.get("accessCode");
            
            String sql = "UPDATE tickets SET status = 'USED', used_at = NOW() WHERE access_code = ? AND status = 'ISSUED'";
            int rowsUpdated = jdbcTemplate.update(sql, accessCode);
            
            if (rowsUpdated > 0) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Ticket marcado como usado exitosamente"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "No se pudo marcar el ticket como usado"
                ));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error usando ticket: " + e.getMessage()
            ));
        }
    }
    
    @PutMapping("/tournaments/{id}/publish")
    public ResponseEntity<Map<String, Object>> publishTournament(@PathVariable String id) {
        try {
            String sql = "UPDATE tournaments SET status = 'PUBLISHED' WHERE id = ? AND status = 'DRAFT'";
            int rowsUpdated = jdbcTemplate.update(sql, UUID.fromString(id));
            
            if (rowsUpdated > 0) {
                return ResponseEntity.ok(Map.of(
                    "message", "Torneo publicado exitosamente",
                    "id", id,
                    "status", "PUBLISHED"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "No se pudo publicar el torneo. Verifique que esté en estado DRAFT."
                ));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Error publicando torneo: " + e.getMessage()
            ));
        }
    }
    
    private String generateAccessCode() {
        return java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}