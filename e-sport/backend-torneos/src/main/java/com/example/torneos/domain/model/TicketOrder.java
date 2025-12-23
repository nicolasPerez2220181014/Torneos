package com.example.torneos.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TicketOrder {
    private UUID id;
    private UUID tournamentId;
    private UUID userId;
    private UUID stageId;
    private Integer quantity;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;

    public enum OrderStatus {
        PENDING, APPROVED, REJECTED
    }

    public TicketOrder() {}

    public TicketOrder(UUID tournamentId, UUID userId, UUID stageId, 
                      Integer quantity, BigDecimal totalAmount) {
        this.tournamentId = tournamentId;
        this.userId = userId;
        this.stageId = stageId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    // Business methods
    public void approve() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be approved");
        }
        this.status = OrderStatus.APPROVED;
    }
    
    public void reject() {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be rejected");
        }
        this.status = OrderStatus.REJECTED;
    }
    
    public boolean isPending() {
        return status == OrderStatus.PENDING;
    }
    
    public boolean isApproved() {
        return status == OrderStatus.APPROVED;
    }
    
    public boolean isRejected() {
        return status == OrderStatus.REJECTED;
    }
    
    public boolean isFree() {
        return totalAmount.compareTo(BigDecimal.ZERO) == 0;
    }
    
    public BigDecimal calculateRefundAmount() {
        return isApproved() ? totalAmount : BigDecimal.ZERO;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getTournamentId() { return tournamentId; }
    public void setTournamentId(UUID tournamentId) { this.tournamentId = tournamentId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getStageId() { return stageId; }
    public void setStageId(UUID stageId) { this.stageId = stageId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}