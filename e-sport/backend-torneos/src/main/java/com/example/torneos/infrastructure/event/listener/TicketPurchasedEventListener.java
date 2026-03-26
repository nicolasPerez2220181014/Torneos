package com.example.torneos.infrastructure.event.listener;

import com.example.torneos.domain.event.TicketPurchasedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class TicketPurchasedEventListener {

    private static final Logger log = LoggerFactory.getLogger(TicketPurchasedEventListener.class);

    @Async
    @EventListener
    public void handleTicketPurchased(TicketPurchasedEvent event) {
        log.info("Handling TicketPurchasedEvent: orderId={}, tournamentId={}, userId={}, quantity={}, amount={}", 
            event.getOrderId(), 
            event.getTournamentId(), 
            event.getUserId(), 
            event.getQuantity(), 
            event.getTotalAmount());

        sendPurchaseConfirmationEmail(event);
        updateTournamentStatistics(event);
    }

    private void sendPurchaseConfirmationEmail(TicketPurchasedEvent event) {
        log.debug("Sending purchase confirmation email for order: {}", event.getOrderId());
    }

    private void updateTournamentStatistics(TicketPurchasedEvent event) {
        log.debug("Updating tournament statistics for tournament: {}", event.getTournamentId());
    }
}
