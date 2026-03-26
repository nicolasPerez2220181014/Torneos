package com.example.torneos.infrastructure.event.listener;

import com.example.torneos.domain.event.TournamentPublishedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class TournamentPublishedEventListener {

    private static final Logger log = LoggerFactory.getLogger(TournamentPublishedEventListener.class);

    @Async
    @EventListener
    public void handleTournamentPublished(TournamentPublishedEvent event) {
        log.info("Handling TournamentPublishedEvent: tournamentId={}, name='{}', organizerId={}, isPaid={}, startDate={}", 
            event.getTournamentId(), 
            event.getTournamentName(), 
            event.getOrganizerId(), 
            event.isPaid(), 
            event.getStartDateTime());

        notifySubscribers(event);
        indexForSearch(event);
        sendOrganizerNotification(event);
    }

    private void notifySubscribers(TournamentPublishedEvent event) {
        log.debug("Notifying subscribers about tournament: {}", event.getTournamentName());
    }

    private void indexForSearch(TournamentPublishedEvent event) {
        log.debug("Indexing tournament for search: {}", event.getTournamentId());
    }

    private void sendOrganizerNotification(TournamentPublishedEvent event) {
        log.debug("Sending notification to organizer: {}", event.getOrganizerId());
    }
}
