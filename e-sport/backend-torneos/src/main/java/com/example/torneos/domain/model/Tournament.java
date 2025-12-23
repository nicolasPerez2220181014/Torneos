package com.example.torneos.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Tournament {
    private UUID id;
    private UUID organizerId;
    private UUID categoryId;
    private UUID gameTypeId;
    private String name;
    private String description;
    private boolean isPaid;
    private Integer maxFreeCapacity;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private TournamentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Tournament() {}

    public Tournament(UUID organizerId, UUID categoryId, UUID gameTypeId, String name, 
                     String description, boolean isPaid, Integer maxFreeCapacity,
                     LocalDateTime startDateTime, LocalDateTime endDateTime) {
        this.id = UUID.randomUUID();
        this.organizerId = organizerId;
        this.categoryId = categoryId;
        this.gameTypeId = gameTypeId;
        this.name = name;
        this.description = description;
        this.isPaid = isPaid;
        this.maxFreeCapacity = maxFreeCapacity;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.status = TournamentStatus.DRAFT;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Business methods
    public void publish() {
        if (status != TournamentStatus.DRAFT) {
            throw new IllegalStateException("Only draft tournaments can be published");
        }
        this.status = TournamentStatus.PUBLISHED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void cancel() {
        if (status == TournamentStatus.FINISHED) {
            throw new IllegalStateException("Cannot cancel finished tournament");
        }
        this.status = TournamentStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void finish() {
        if (status != TournamentStatus.PUBLISHED) {
            throw new IllegalStateException("Only published tournaments can be finished");
        }
        this.status = TournamentStatus.FINISHED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean canBeModified() {
        return status == TournamentStatus.DRAFT;
    }
    
    public boolean isActive() {
        return status == TournamentStatus.PUBLISHED;
    }
    
    public boolean hasStarted() {
        return LocalDateTime.now().isAfter(startDateTime);
    }
    
    public boolean hasEnded() {
        return LocalDateTime.now().isAfter(endDateTime);
    }
    
    public void updateDetails(String name, String description, Integer maxFreeCapacity) {
        if (!canBeModified()) {
            throw new IllegalStateException("Cannot modify published tournament");
        }
        this.name = name;
        this.description = description;
        this.maxFreeCapacity = maxFreeCapacity;
        this.updatedAt = LocalDateTime.now();
    }

    public enum TournamentStatus {
        DRAFT, PUBLISHED, FINISHED, CANCELLED
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOrganizerId() { return organizerId; }
    public void setOrganizerId(UUID organizerId) { this.organizerId = organizerId; }

    public UUID getCategoryId() { return categoryId; }
    public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }

    public UUID getGameTypeId() { return gameTypeId; }
    public void setGameTypeId(UUID gameTypeId) { this.gameTypeId = gameTypeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }

    public Integer getMaxFreeCapacity() { return maxFreeCapacity; }
    public void setMaxFreeCapacity(Integer maxFreeCapacity) { this.maxFreeCapacity = maxFreeCapacity; }

    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }

    public TournamentStatus getStatus() { return status; }
    public void setStatus(TournamentStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}