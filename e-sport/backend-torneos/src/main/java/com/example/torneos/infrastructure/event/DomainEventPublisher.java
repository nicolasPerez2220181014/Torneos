package com.example.torneos.infrastructure.event;

import com.example.torneos.domain.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class DomainEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(DomainEventPublisher.class);
    private final ApplicationEventPublisher eventPublisher;

    public DomainEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publish(DomainEvent event) {
        log.info("Publishing event: {} [eventId={}, occurredOn={}]", 
            event.getClass().getSimpleName(), 
            event.getEventId(), 
            event.getOccurredOn());
        
        eventPublisher.publishEvent(event);
        
        log.debug("Event published successfully: {}", event.getClass().getSimpleName());
    }
}
