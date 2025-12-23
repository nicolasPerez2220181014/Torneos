package com.example.torneos.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tournament_admins")
public class TournamentAdminEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "tournament_id", nullable = false)
    private UUID tournamentId;
    
    @Column(name = "sub_admin_user_id", nullable = false)
    private UUID subAdminUserId;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public TournamentAdminEntity() {}

    public TournamentAdminEntity(UUID tournamentId, UUID subAdminUserId) {
        this.tournamentId = tournamentId;
        this.subAdminUserId = subAdminUserId;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getTournamentId() { return tournamentId; }
    public void setTournamentId(UUID tournamentId) { this.tournamentId = tournamentId; }

    public UUID getSubAdminUserId() { return subAdminUserId; }
    public void setSubAdminUserId(UUID subAdminUserId) { this.subAdminUserId = subAdminUserId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}