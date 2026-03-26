package com.example.torneos.infrastructure.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MetricsService {

    private final Counter ticketsCreatedCounter;
    private final Counter tournamentsPublishedCounter;

    public MetricsService(MeterRegistry meterRegistry) {
        this.ticketsCreatedCounter = Counter.builder("tickets.created")
                .description("Total number of tickets created")
                .tag("type", "ticket")
                .register(meterRegistry);

        this.tournamentsPublishedCounter = Counter.builder("tournaments.published")
                .description("Total number of tournaments published")
                .tag("type", "tournament")
                .register(meterRegistry);
    }

    public void incrementTicketsCreated() {
        ticketsCreatedCounter.increment();
    }

    public void incrementTicketsCreated(double amount) {
        ticketsCreatedCounter.increment(amount);
    }

    public void incrementTournamentsPublished() {
        tournamentsPublishedCounter.increment();
    }
}
