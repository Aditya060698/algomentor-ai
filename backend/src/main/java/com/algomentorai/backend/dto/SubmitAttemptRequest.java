package com.algomentorai.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SubmitAttemptRequest(
        @NotNull(message = "userId is required")
        Long userId,

        @NotNull(message = "questionId is required")
        Long questionId,

        @NotBlank(message = "submittedAnswer must not be blank")
        @Size(max = 4000, message = "submittedAnswer must be at most 4000 characters")
        String submittedAnswer
) {
}
