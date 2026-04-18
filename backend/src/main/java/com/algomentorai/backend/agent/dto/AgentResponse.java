package com.algomentorai.backend.agent.dto;

import java.util.List;

public record AgentResponse(
        AgentQuestionType classifiedType,
        String explanation,
        String approach,
        String code,
        List<String> edgeCases,
        List<String> followUpQuestions,
        List<RetrievedConcept> retrievedConcepts
) {
}
