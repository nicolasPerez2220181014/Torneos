package com.example.torneos.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "idempotency_keys")
public class IdempotencyKeyEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    @Column(name = "idempotency_key", unique = true, nullable = false)
    private String key;
    
    @Column(name = "response_body", columnDefinition = "TEXT")
    private String response;
    
    @Column(name = "status_code")
    private Integer statusCode;
    
    @Column(name = "expires_at")
    private Instant expiresAt;
    
    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    // Constructors
    public IdempotencyKeyEntity() {}

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public Integer getStatusCode() { return statusCode; }
    public void setStatusCode(Integer statusCode) { this.statusCode = statusCode; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}