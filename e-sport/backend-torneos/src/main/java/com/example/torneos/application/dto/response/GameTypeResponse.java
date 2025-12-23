package com.example.torneos.application.dto.response;

import java.util.UUID;

public record GameTypeResponse(
    UUID id,
    String name,
    boolean active
) {}