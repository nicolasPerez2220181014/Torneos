package com.example.torneos.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TicketSaleStage {
    private UUID id;
    private UUID tournamentId;
    private StageType stageType;
    private BigDecimal price;
    private Integer capacity;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private boolean active;

    public enum StageType {
        EARLY_BIRD, REGULAR, LAST_MINUTE
    }

    public TicketSaleStage() {}

    public TicketSaleStage(UUID tournamentId, StageType stageType, BigDecimal price, 
                          Integer capacity, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.tournamentId = tournamentId;
        this.stageType = stageType;
        this.price = price;
        this.capacity = capacity;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.active = true;
    }
    
    // Business methods
    public boolean hasAvailableCapacity(int requestedQuantity) {
        return this.capacity >= requestedQuantity;
    }
    
    public BigDecimal calculateTotalPrice(int quantity) {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
    
    public boolean isActiveAt(LocalDateTime dateTime) {
        return active && 
               !dateTime.isBefore(startDateTime) && 
               !dateTime.isAfter(endDateTime);
    }
    
    public boolean isCurrentlyActive() {
        return isActiveAt(LocalDateTime.now());
    }
    
    public void deactivate() {
        this.active = false;
    }
    
    public void activate() {
        if (LocalDateTime.now().isAfter(endDateTime)) {
            throw new IllegalStateException("Cannot activate expired stage");
        }
        this.active = true;
    }
    
    public boolean isEarlyBird() {
        return stageType == StageType.EARLY_BIRD;
    }
    
    public boolean isFree() {
        return price.compareTo(BigDecimal.ZERO) == 0;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getTournamentId() { return tournamentId; }
    public void setTournamentId(UUID tournamentId) { this.tournamentId = tournamentId; }

    public StageType getStageType() { return stageType; }
    public void setStageType(StageType stageType) { this.stageType = stageType; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}