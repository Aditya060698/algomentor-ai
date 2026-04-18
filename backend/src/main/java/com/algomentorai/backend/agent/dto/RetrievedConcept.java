package com.algomentorai.backend.agent.dto;

import java.util.List;

public record RetrievedConcept(
        String title,
        String content,
        List<String> keywords,
        double similarityScore
) {
}
