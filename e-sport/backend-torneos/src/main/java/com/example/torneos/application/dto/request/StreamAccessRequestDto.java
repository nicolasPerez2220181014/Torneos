package com.example.torneos.application.dto.request;

import jakarta.validation.constraints.NotNull;

public class StreamAccessRequestDto {
    @NotNull(message = "Access type is required")
    private String accessType; // FREE or PAID
    
    private String ticketAccessCode; // Required for PAID access

    public StreamAccessRequestDto() {}

    public String getAccessType() { return accessType; }
    public void setAccessType(String accessType) { this.accessType = accessType; }

    public String getTicketAccessCode() { return ticketAccessCode; }
    public void setTicketAccessCode(String ticketAccessCode) { this.ticketAccessCode = ticketAccessCode; }
}