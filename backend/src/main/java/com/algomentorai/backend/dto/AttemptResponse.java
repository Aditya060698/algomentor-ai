package com.algomentorai.backend.dto;

import com.algomentorai.backend.entity.AttemptStatus;

import java.time.Instant;

public record AttemptResponse(
        Long id,
        Long userId,
        Long questionId,
        Integer score,
        String automatedFeedback,
        AttemptStatus status,
        Instant createdAt
) {
}
