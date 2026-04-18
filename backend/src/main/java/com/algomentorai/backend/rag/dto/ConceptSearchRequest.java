package com.algomentorai.backend.rag.dto;

import com.algomentorai.backend.rag.entity.ConceptNoteType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ConceptSearchRequest(
        @NotBlank(message = "query must not be blank")
        String query,

        @Min(value = 1, message = "topK must be at least 1")
        @Max(value = 10, message = "topK must be at most 10")
        Integer topK,

        ConceptNoteType type
) {
}
