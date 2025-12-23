package com.example.torneos.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class BusinessMetrics {
    
    private final Counter tournamentsCreated;
    private final Counter tournamentsPublished;
    private final Counter ticketOrdersCreated;
    private final Counter ticketOrdersApproved;
    private final Timer orderProcessingTime;
    
    public BusinessMetrics(MeterRegistry meterRegistry) {
        this.tournamentsCreated = Counter.builder("tournaments.created")
            .description("Total tournaments created")
            .register(meterRegistry);
            
        this.tournamentsPublished = Counter.builder("tournaments.published")
            .description("Total tournaments published")
            .register(meterRegistry);
            
        this.ticketOrdersCreated = Counter.builder("ticket.orders.created")
            .description("Total ticket orders created")
            .register(meterRegistry);
            
        this.ticketOrdersApproved = Counter.builder("ticket.orders.approved")
            .description("Total ticket orders approved")
            .register(meterRegistry);
            
        this.orderProcessingTime = Timer.builder("order.processing.time")
            .description("Time to process ticket orders")
            .register(meterRegistry);
    }
    
    public void incrementTournamentsCreated() {
        tournamentsCreated.increment();
    }
    
    public void incrementTournamentsPublished() {
        tournamentsPublished.increment();
    }
    
    public void incrementTicketOrdersCreated() {
        ticketOrdersCreated.increment();
    }
    
    public void incrementTicketOrdersApproved() {
        ticketOrdersApproved.increment();
    }
    
    public Timer.Sample startOrderProcessing() {
        return Timer.start();
    }
    
    public void recordOrderProcessingTime(Timer.Sample sample) {
        sample.stop(orderProcessingTime);
    }
}