package com.example.torneos.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateStreamUrlRequestDto {
    @NotBlank(message = "Stream URL is required")
    @Size(max = 500, message = "Stream URL cannot exceed 500 characters")
    private String streamUrl;

    public UpdateStreamUrlRequestDto() {}

    public String getStreamUrl() { return streamUrl; }
    public void setStreamUrl(String streamUrl) { this.streamUrl = streamUrl; }
}