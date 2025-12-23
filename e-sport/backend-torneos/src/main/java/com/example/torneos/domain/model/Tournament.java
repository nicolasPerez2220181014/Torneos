package com.example.torneos.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad de dominio que representa un torneo de e-sports.
 * Contiene toda la información necesaria para gestionar torneos,
 * incluyendo fechas, participantes y estados del torneo.
 * 
 * @author Equipo de Desarrollo
 * @version 1.0
 */
public class Tournament {
    
    // Identificador único del torneo
    private UUID id;
    
    // ID del usuario organizador del torneo
    private UUID organizerId;
    
    // ID de la categoría del torneo (ej: Profesional, Amateur)
    private UUID categoryId;
    
    // ID del tipo de juego (ej: Valorant, League of Legends)
    private UUID gameTypeId;
    
    // Nombre descriptivo del torneo
    private String name;
    
    // Descripción detallada del torneo
    private String description;
    
    // Indica si el torneo requiere pago para participar
    private boolean isPaid;
    
    // Capacidad máxima de participantes gratuitos
    private Integer maxFreeCapacity;
    
    // Fecha y hora de inicio del torneo
    private LocalDateTime startDateTime;
    
    // Fecha y hora de finalización del torneo
    private LocalDateTime endDateTime;
    
    // Estado actual del torneo
    private TournamentStatus status;
    
    // Timestamp de creación del registro
    private LocalDateTime createdAt;
    
    // Timestamp de última actualización
    private LocalDateTime updatedAt;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public Tournament() {}

    /**
     * Constructor para crear un nuevo torneo.
     * Inicializa el torneo en estado DRAFT con timestamps actuales.
     * 
     * @param organizerId ID del organizador
     * @param categoryId ID de la categoría
     * @param gameTypeId ID del tipo de juego
     * @param name Nombre del torneo
     * @param description Descripción del torneo
     * @param isPaid Si requiere pago
     * @param maxFreeCapacity Capacidad máxima gratuita
     * @param startDateTime Fecha de inicio
     * @param endDateTime Fecha de fin
     */
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
    
    /**
     * Publica el torneo, cambiando su estado de DRAFT a PUBLISHED.
     * Solo los torneos en estado DRAFT pueden ser publicados.
     * 
     * @throws IllegalStateException si el torneo no está en estado DRAFT
     */
    public void publish() {
        if (status != TournamentStatus.DRAFT) {
            throw new IllegalStateException("Solo los torneos en borrador pueden ser publicados");
        }
        this.status = TournamentStatus.PUBLISHED;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Cancela el torneo.
     * Los torneos finalizados no pueden ser cancelados.
     * 
     * @throws IllegalStateException si el torneo ya está finalizado
     */
    public void cancel() {
        if (status == TournamentStatus.FINISHED) {
            throw new IllegalStateException("No se puede cancelar un torneo finalizado");
        }
        this.status = TournamentStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Finaliza el torneo.
     * Solo los torneos publicados pueden ser finalizados.
     * 
     * @throws IllegalStateException si el torneo no está publicado
     */
    public void finish() {
        if (status != TournamentStatus.PUBLISHED) {
            throw new IllegalStateException("Solo los torneos publicados pueden ser finalizados");
        }
        this.status = TournamentStatus.FINISHED;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Verifica si el torneo puede ser modificado.
     * Solo los torneos en estado DRAFT pueden ser modificados.
     * 
     * @return true si puede ser modificado, false en caso contrario
     */
    public boolean canBeModified() {
        return status == TournamentStatus.DRAFT;
    }
    
    /**
     * Verifica si el torneo está activo (publicado).
     * 
     * @return true si está activo, false en caso contrario
     */
    public boolean isActive() {
        return status == TournamentStatus.PUBLISHED;
    }
    
    /**
     * Verifica si el torneo ya ha comenzado.
     * 
     * @return true si ya comenzó, false en caso contrario
     */
    public boolean hasStarted() {
        return LocalDateTime.now().isAfter(startDateTime);
    }
    
    /**
     * Verifica si el torneo ya ha terminado.
     * 
     * @return true si ya terminó, false en caso contrario
     */
    public boolean hasEnded() {
        return LocalDateTime.now().isAfter(endDateTime);
    }
    
    /**
     * Actualiza los detalles básicos del torneo.
     * Solo se puede actualizar si el torneo está en estado DRAFT.
     * 
     * @param name Nuevo nombre
     * @param description Nueva descripción
     * @param maxFreeCapacity Nueva capacidad gratuita
     * @throws IllegalStateException si el torneo no puede ser modificado
     */
    public void updateDetails(String name, String description, Integer maxFreeCapacity) {
        if (!canBeModified()) {
            throw new IllegalStateException("No se puede modificar un torneo publicado");
        }
        this.name = name;
        this.description = description;
        this.maxFreeCapacity = maxFreeCapacity;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Estados posibles de un torneo durante su ciclo de vida.
     */
    public enum TournamentStatus {
        /** Torneo en borrador, aún no publicado */
        DRAFT,
        
        /** Torneo publicado y disponible para inscripciones */
        PUBLISHED,
        
        /** Torneo finalizado */
        FINISHED,
        
        /** Torneo cancelado */
        CANCELLED
    }

    // === GETTERS Y SETTERS ===
    
    public UUID getId() { 
        return id; 
    }
    
    public void setId(UUID id) { 
        this.id = id; 
    }

    public UUID getOrganizerId() { 
        return organizerId; 
    }
    
    public void setOrganizerId(UUID organizerId) { 
        this.organizerId = organizerId; 
    }

    public UUID getCategoryId() { 
        return categoryId; 
    }
    
    public void setCategoryId(UUID categoryId) { 
        this.categoryId = categoryId; 
    }

    public UUID getGameTypeId() { 
        return gameTypeId; 
    }
    
    public void setGameTypeId(UUID gameTypeId) { 
        this.gameTypeId = gameTypeId; 
    }

    public String getName() { 
        return name; 
    }
    
    public void setName(String name) { 
        this.name = name; 
    }

    public String getDescription() { 
        return description; 
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }

    public boolean isPaid() { 
        return isPaid; 
    }
    
    public void setPaid(boolean paid) { 
        isPaid = paid; 
    }

    public Integer getMaxFreeCapacity() { 
        return maxFreeCapacity; 
    }
    
    public void setMaxFreeCapacity(Integer maxFreeCapacity) { 
        this.maxFreeCapacity = maxFreeCapacity; 
    }

    public LocalDateTime getStartDateTime() { 
        return startDateTime; 
    }
    
    public void setStartDateTime(LocalDateTime startDateTime) { 
        this.startDateTime = startDateTime; 
    }

    public LocalDateTime getEndDateTime() { 
        return endDateTime; 
    }
    
    public void setEndDateTime(LocalDateTime endDateTime) { 
        this.endDateTime = endDateTime; 
    }

    public TournamentStatus getStatus() { 
        return status; 
    }
    
    public void setStatus(TournamentStatus status) { 
        this.status = status; 
    }

    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    
    public void setCreatedAt(LocalDateTime createdAt) { 
        this.createdAt = createdAt; 
    }

    public LocalDateTime getUpdatedAt() { 
        return updatedAt; 
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) { 
        this.updatedAt = updatedAt; 
    }
}