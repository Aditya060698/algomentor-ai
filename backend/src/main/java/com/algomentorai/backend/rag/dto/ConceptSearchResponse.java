package com.algomentorai.backend.rag.dto;

import com.algomentorai.backend.rag.entity.ConceptNoteType;

import java.util.List;

public record ConceptSearchResponse(
        Long id,
        String title,
        ConceptNoteType type,
        String content,
        List<String> keywords,
        double similarityScore
) {
}
