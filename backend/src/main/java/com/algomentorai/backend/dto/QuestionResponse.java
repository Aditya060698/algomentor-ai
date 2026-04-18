package com.algomentorai.backend.dto;

import com.algomentorai.backend.entity.DifficultyLevel;
import com.algomentorai.backend.entity.QuestionType;

import java.time.Instant;
import java.util.List;

public record QuestionResponse(
        Long id,
        String title,
        QuestionType type,
        DifficultyLevel difficulty,
        List<String> expectedConcepts,
        Instant createdAt
) {
}
