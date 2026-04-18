package com.algomentorai.backend.evaluation.service;

import com.algomentorai.backend.dsa.heap.FeedbackPriorityQueue;
import com.algomentorai.backend.dsa.heap.RankedFeedback;
import com.algomentorai.backend.dsa.scoring.ScoringEngine;
import com.algomentorai.backend.dsa.trie.PatternTrie;
import com.algomentorai.backend.evaluation.dto.DsaEvaluationResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class DsaEvaluationService {

    private final PatternTrie patternTrie;
    private final FeedbackPriorityQueue feedbackPriorityQueue;
    private final ScoringEngine scoringEngine;

    public DsaEvaluationService(
            PatternTrie patternTrie,
            FeedbackPriorityQueue feedbackPriorityQueue,
            ScoringEngine scoringEngine
    ) {
        this.patternTrie = patternTrie;
        this.feedbackPriorityQueue = feedbackPriorityQueue;
        this.scoringEngine = scoringEngine;
    }

    public DsaEvaluationResult evaluate(String submittedAnswer, List<String> expectedConcepts, String sampleAnswer) {
        Set<String> normalizedAnswerTokens = toNormalizedTokenSet(submittedAnswer);
        List<String> matchedConcepts = new ArrayList<>();
        List<String> missingConcepts = new ArrayList<>();

        for (String concept : expectedConcepts) {
            if (containsConcept(normalizedAnswerTokens, concept)) {
                matchedConcepts.add(concept);
            } else {
                missingConcepts.add(concept);
            }
        }

        Set<String> detectedPatterns = patternTrie.detectPatterns(submittedAnswer);
        ComplexityCheckResult complexityCheck = checkComplexityMention(submittedAnswer, sampleAnswer);

        int finalScore = scoringEngine.calculateScore(
                expectedConcepts.size(),
                matchedConcepts.size(),
                complexityCheck.missing(),
                complexityCheck.incorrect(),
                detectedPatterns.size()
        );

        List<String> rankedFeedback = feedbackPriorityQueue.rank(buildFeedbackItems(
                matchedConcepts,
                missingConcepts,
                detectedPatterns,
                complexityCheck
        ));

        String summaryFeedback = buildSummaryFeedback(matchedConcepts, missingConcepts, detectedPatterns, complexityCheck, finalScore, rankedFeedback);

        return new DsaEvaluationResult(
                finalScore,
                matchedConcepts,
                missingConcepts,
                new ArrayList<>(detectedPatterns),
                complexityCheck.feedback(),
                rankedFeedback,
                summaryFeedback
        );
    }

    private Set<String> toNormalizedTokenSet(String content) {
        Set<String> tokens = new HashSet<>();
        for (String token : content.toLowerCase(Locale.ROOT).split("\\W+")) {
            String normalized = token.trim();
            if (!normalized.isBlank()) {
                tokens.add(normalized);
            }
        }
        return tokens;
    }

    private boolean containsConcept(Set<String> answerTokens, String concept) {
        Set<String> conceptTokens = toNormalizedTokenSet(concept);
        return answerTokens.containsAll(conceptTokens);
    }

    private ComplexityCheckResult checkComplexityMention(String submittedAnswer, String sampleAnswer) {
        String normalizedAnswer = submittedAnswer.toLowerCase(Locale.ROOT);
        String normalizedSample = sampleAnswer.toLowerCase(Locale.ROOT);

        boolean answerMentionsON = mentionsAny(normalizedAnswer, "o(n)", "linear");
        boolean answerMentionsON2 = mentionsAny(normalizedAnswer, "o(n^2)", "o(n2)", "quadratic");
        boolean sampleMentionsON = mentionsAny(normalizedSample, "o(n)", "linear");
        boolean sampleMentionsON2 = mentionsAny(normalizedSample, "o(n^2)", "o(n2)", "quadratic");

        if (answerMentionsON2 && sampleMentionsON && !sampleMentionsON2) {
            return new ComplexityCheckResult(
                    true,
                    false,
                    "Your complexity statement looks incorrect. The expected approach suggests linear time, but your answer mentions quadratic complexity."
            );
        }

        if (!answerMentionsON && !answerMentionsON2) {
            return new ComplexityCheckResult(
                    false,
                    true,
                    "Your answer does not mention time complexity. In interviews, explicitly stating complexity is important."
            );
        }

        return new ComplexityCheckResult(
                false,
                false,
                "Complexity discussion is present and does not conflict with the expected approach."
        );
    }

    private boolean mentionsAny(String content, String... options) {
        for (String option : options) {
            if (content.contains(option)) {
                return true;
            }
        }
        return false;
    }

    private List<RankedFeedback> buildFeedbackItems(
            List<String> matchedConcepts,
            List<String> missingConcepts,
            Set<String> detectedPatterns,
            ComplexityCheckResult complexityCheck
    ) {
        List<RankedFeedback> feedbackItems = new ArrayList<>();

        if (!missingConcepts.isEmpty()) {
            feedbackItems.add(new RankedFeedback(
                    "Missing concepts: " + String.join(", ", missingConcepts),
                    100
            ));
        }

        if (complexityCheck.incorrect()) {
            feedbackItems.add(new RankedFeedback(complexityCheck.feedback(), 95));
        } else if (complexityCheck.missing()) {
            feedbackItems.add(new RankedFeedback(complexityCheck.feedback(), 85));
        } else {
            feedbackItems.add(new RankedFeedback(complexityCheck.feedback(), 40));
        }

        if (!detectedPatterns.isEmpty()) {
            feedbackItems.add(new RankedFeedback(
                    "Detected DSA patterns: " + String.join(", ", detectedPatterns),
                    60
            ));
        }

        if (!matchedConcepts.isEmpty()) {
            feedbackItems.add(new RankedFeedback(
                    "Matched concepts: " + String.join(", ", matchedConcepts),
                    50
            ));
        }

        return feedbackItems;
    }

    private String buildSummaryFeedback(
            List<String> matchedConcepts,
            List<String> missingConcepts,
            Set<String> detectedPatterns,
            ComplexityCheckResult complexityCheck,
            int score,
            List<String> rankedFeedback
    ) {
        StringBuilder feedback = new StringBuilder();
        feedback.append("Matched concepts: ")
                .append(matchedConcepts.isEmpty() ? "none" : String.join(", ", matchedConcepts))
                .append(". ");

        if (!missingConcepts.isEmpty()) {
            feedback.append("Missing concepts: ")
                    .append(String.join(", ", missingConcepts))
                    .append(". ");
        }

        if (!detectedPatterns.isEmpty()) {
            feedback.append("Detected patterns: ")
                    .append(String.join(", ", detectedPatterns))
                    .append(". ");
        }

        feedback.append(complexityCheck.feedback())
                .append(" Final score: ")
                .append(score)
                .append("%. ");

        if (!rankedFeedback.isEmpty()) {
            feedback.append("Top feedback: ")
                    .append(rankedFeedback.get(0))
                    .append(".");
        }

        return feedback.toString();
    }

    private record ComplexityCheckResult(boolean incorrect, boolean missing, String feedback) {
    }
}
