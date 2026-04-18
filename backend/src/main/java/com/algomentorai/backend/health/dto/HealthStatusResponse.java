package com.algomentorai.backend.health.dto;

public record HealthStatusResponse(
        String service,
        String status
) {
}
