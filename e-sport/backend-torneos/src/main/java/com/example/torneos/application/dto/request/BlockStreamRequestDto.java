package com.example.torneos.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public class BlockStreamRequestDto {
    @NotBlank(message = "Block reason is required")
    private String blockReason;

    public BlockStreamRequestDto() {}

    public String getBlockReason() { return blockReason; }
    public void setBlockReason(String blockReason) { this.blockReason = blockReason; }
}