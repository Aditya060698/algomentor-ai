package com.algomentorai.backend.evaluation.dto;

import java.util.List;

public record SystemDesignEvaluationResult(
        int score,
        List<String> matchedComponents,
        List<String> missingComponents,
        List<String> scalabilityIssues,
        List<String> bottlenecks,
        List<String> tradeOffFeedback,
        List<String> rankedFeedback,
        String summaryFeedback
) {
}
