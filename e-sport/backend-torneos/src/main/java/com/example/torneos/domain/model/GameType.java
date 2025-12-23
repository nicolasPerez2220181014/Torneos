package com.example.torneos.domain.model;

import java.util.UUID;

public class GameType {
    private UUID id;
    private String name;
    private boolean active;

    public GameType() {}

    public GameType(UUID id, String name, boolean active) {
        this.id = id;
        this.name = name;
        this.active = active;
    }

    public GameType(String name) {
        this.name = name;
        this.active = true;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}