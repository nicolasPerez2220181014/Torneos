package com.example.torneos.infrastructure.observability;

import com.example.torneos.domain.event.TicketPurchasedEvent;
import com.example.torneos.domain.event.TournamentPublishedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class MetricsEventListener {

    private static final Logger log = LoggerFactory.getLogger(MetricsEventListener.class);
    private final MetricsService metricsService;

    public MetricsEventListener(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @EventListener
    public void onTicketPurchased(TicketPurchasedEvent event) {
        metricsService.incrementTicketsCreated(event.getQuantity());
        log.debug("Metric incremented: tickets.created by {}", event.getQuantity());
    }

    @EventListener
    public void onTournamentPublished(TournamentPublishedEvent event) {
        metricsService.incrementTournamentsPublished();
        log.debug("Metric incremented: tournaments.published");
    }
}
