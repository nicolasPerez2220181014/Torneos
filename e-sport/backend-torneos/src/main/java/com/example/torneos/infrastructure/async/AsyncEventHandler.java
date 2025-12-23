package com.example.torneos.infrastructure.async;

import com.example.torneos.domain.event.TournamentPublished;
import com.example.torneos.domain.event.TicketOrderApproved;
import com.example.torneos.infrastructure.audit.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AsyncEventHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(AsyncEventHandler.class);
    private final AuditService auditService;
    
    public AsyncEventHandler(AuditService auditService) {
        this.auditService = auditService;
    }
    
    @Async("eventExecutor")
    @EventListener
    public void handleTournamentPublishedAsync(TournamentPublished event) {
        logger.info("Processing TournamentPublished event asynchronously: {}", event.tournamentId());
        
        // Async operations
        auditService.logTournamentPublished(event.tournamentId(), event.organizerId());
        
        // Additional async tasks (notifications, indexing, etc.)
        logger.info("TournamentPublished event processed");
    }
    
    @Async("eventExecutor")
    @EventListener
    public void handleTicketOrderApprovedAsync(TicketOrderApproved event) {
        logger.info("Processing TicketOrderApproved event asynchronously: {}", event.orderId());
        
        // Async operations
        // Send notifications, generate tickets, etc.
        
        logger.info("TicketOrderApproved event processed");
    }
}