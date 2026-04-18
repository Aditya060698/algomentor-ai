package com.algomentorai.backend.evaluation.dto;

import java.util.List;

public record DsaEvaluationResult(
        int score,
        List<String> matchedConcepts,
        List<String> missingConcepts,
        List<String> detectedPatterns,
        String complexityFeedback,
        List<String> rankedFeedback,
        String summaryFeedback
) {
}
