package com.example.torneos.infrastructure.event;

import com.example.torneos.domain.event.TournamentPublished;
import com.example.torneos.domain.event.TicketOrderApproved;
import com.example.torneos.domain.event.TicketsGenerated;
import com.example.torneos.domain.event.TournamentCancelled;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class DomainEventHandler {
    
    @EventListener
    public void handle(TournamentPublished event) {
        // Handle tournament published - could send notifications, update search index, etc.
        System.out.println("Tournament published: " + event.tournamentName());
    }
    
    @EventListener
    public void handle(TicketOrderApproved event) {
        // Handle order approved - could send confirmation email, update analytics, etc.
        System.out.println("Order approved: " + event.orderId() + " for " + event.quantity() + " tickets");
    }
    
    @EventListener
    public void handle(TicketsGenerated event) {
        // Handle tickets generated - could send tickets via email, SMS, etc.
        System.out.println("Generated " + event.accessCodes().size() + " tickets for order: " + event.orderId());
    }
    
    @EventListener
    public void handle(TournamentCancelled event) {
        // Handle tournament cancelled - could refund orders, send notifications, etc.
        System.out.println("Tournament cancelled: " + event.tournamentId() + " - " + event.reason());
    }
}