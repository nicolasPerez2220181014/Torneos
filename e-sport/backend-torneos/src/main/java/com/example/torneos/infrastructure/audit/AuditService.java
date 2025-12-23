package com.example.torneos.infrastructure.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuditService {
    
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
    
    public void logTournamentCreated(UUID tournamentId, UUID organizerId, String tournamentName) {
        auditLogger.info("TOURNAMENT_CREATED: tournamentId={}, organizerId={}, name={}, timestamp={}", 
            tournamentId, organizerId, tournamentName, Instant.now());
    }
    
    public void logTournamentPublished(UUID tournamentId, UUID userId) {
        auditLogger.info("TOURNAMENT_PUBLISHED: tournamentId={}, userId={}, timestamp={}", 
            tournamentId, userId, Instant.now());
    }
    
    public void logTicketOrderCreated(UUID orderId, UUID tournamentId, UUID userId, int quantity) {
        auditLogger.info("TICKET_ORDER_CREATED: orderId={}, tournamentId={}, userId={}, quantity={}, timestamp={}", 
            orderId, tournamentId, userId, quantity, Instant.now());
    }
    
    public void logTicketOrderApproved(UUID orderId, UUID approvedBy) {
        auditLogger.info("TICKET_ORDER_APPROVED: orderId={}, approvedBy={}, timestamp={}", 
            orderId, approvedBy, Instant.now());
    }
    
    public void logSubAdminAssigned(UUID tournamentId, UUID subAdminId, UUID assignedBy) {
        auditLogger.info("SUBADMIN_ASSIGNED: tournamentId={}, subAdminId={}, assignedBy={}, timestamp={}", 
            tournamentId, subAdminId, assignedBy, Instant.now());
    }
    
    public void logSecurityEvent(String eventType, String details, UUID userId) {
        auditLogger.warn("SECURITY_EVENT: type={}, details={}, userId={}, timestamp={}", 
            eventType, details, userId, Instant.now());
    }
}