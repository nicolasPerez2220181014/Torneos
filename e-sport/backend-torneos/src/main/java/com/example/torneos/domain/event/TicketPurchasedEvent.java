package com.example.torneos.domain.event;

import java.util.UUID;

public class TicketPurchasedEvent extends DomainEvent {
    private final UUID orderId;
    private final UUID tournamentId;
    private final UUID userId;
    private final int quantity;
    private final double totalAmount;

    public TicketPurchasedEvent(UUID orderId, UUID tournamentId, UUID userId, int quantity, double totalAmount) {
        super();
        this.orderId = orderId;
        this.tournamentId = tournamentId;
        this.userId = userId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getTournamentId() {
        return tournamentId;
    }

    public UUID getUserId() {
        return userId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }
}
