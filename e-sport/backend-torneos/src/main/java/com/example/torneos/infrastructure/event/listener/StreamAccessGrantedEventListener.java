package com.example.torneos.infrastructure.event.listener;

import com.example.torneos.domain.event.StreamAccessGrantedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class StreamAccessGrantedEventListener {

    private static final Logger log = LoggerFactory.getLogger(StreamAccessGrantedEventListener.class);

    @Async
    @EventListener
    public void handleStreamAccessGranted(StreamAccessGrantedEvent event) {
        log.info("Handling StreamAccessGrantedEvent: accessId={}, tournamentId={}, userId={}, accessType={}, ticketId={}", 
            event.getAccessId(), 
            event.getTournamentId(), 
            event.getUserId(), 
            event.getAccessType(), 
            event.getTicketId());

        sendAccessConfirmation(event);
        trackAccessMetrics(event);
    }

    private void sendAccessConfirmation(StreamAccessGrantedEvent event) {
        log.debug("Sending stream access confirmation to user: {}", event.getUserId());
    }

    private void trackAccessMetrics(StreamAccessGrantedEvent event) {
        log.debug("Tracking access metrics for tournament: {}, type: {}", 
            event.getTournamentId(), event.getAccessType());
    }
}
