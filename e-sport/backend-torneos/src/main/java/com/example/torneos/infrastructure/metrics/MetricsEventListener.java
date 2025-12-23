package com.example.torneos.infrastructure.metrics;

import com.example.torneos.domain.event.TournamentPublished;
import com.example.torneos.domain.event.TicketOrderApproved;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MetricsEventListener {
    
    private final BusinessMetrics businessMetrics;
    
    public MetricsEventListener(BusinessMetrics businessMetrics) {
        this.businessMetrics = businessMetrics;
    }
    
    @EventListener
    public void handleTournamentPublished(TournamentPublished event) {
        businessMetrics.incrementTournamentsPublished();
    }
    
    @EventListener
    public void handleTicketOrderApproved(TicketOrderApproved event) {
        businessMetrics.incrementTicketOrdersApproved();
    }
}