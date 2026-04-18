package com.algomentorai.backend.agent.dto;

import jakarta.validation.constraints.NotBlank;

public record AgentRequest(
        @NotBlank(message = "query must not be blank")
        String query
) {
}
