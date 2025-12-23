package com.example.torneos.domain.event;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}