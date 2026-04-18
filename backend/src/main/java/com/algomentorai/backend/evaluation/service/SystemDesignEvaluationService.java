package com.algomentorai.backend.evaluation.service;

import com.algomentorai.backend.dsa.heap.FeedbackPriorityQueue;
import com.algomentorai.backend.dsa.heap.RankedFeedback;
import com.algomentorai.backend.dsa.scoring.ScoringEngine;
import com.algomentorai.backend.evaluation.dto.SystemDesignEvaluationResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class SystemDesignEvaluationService {

    private final FeedbackPriorityQueue feedbackPriorityQueue;
    private final ScoringEngine scoringEngine;

    public SystemDesignEvaluationService(
            FeedbackPriorityQueue feedbackPriorityQueue,
            ScoringEngine scoringEngine
    ) {
        this.feedbackPriorityQueue = feedbackPriorityQueue;
        this.scoringEngine = scoringEngine;
    }

    public SystemDesignEvaluationResult evaluate(String submittedAnswer, List<String> expectedComponents) {
        Set<String> normalizedTokens = toNormalizedTokenSet(submittedAnswer);
        List<String> matchedComponents = new ArrayList<>();
        List<String> missingComponents = new ArrayList<>();

        for (String component : expectedComponents) {
            if (containsComponent(normalizedTokens, component)) {
                matchedComponents.add(component);
            } else {
                missingComponents.add(component);
            }
        }

        List<String> scalabilityIssues = detectScalabilityIssues(normalizedTokens);
        List<String> bottlenecks = detectBottlenecks(normalizedTokens);
        List<String> tradeOffFeedback = detectTradeOffGaps(normalizedTokens);

        boolean complexityMissing = tradeOffFeedback.size() > 0;
        boolean complexityIncorrect = false;

        int score = scoringEngine.calculateScore(
                expectedComponents.size(),
                matchedComponents.size(),
                complexityMissing,
                complexityIncorrect,
                matchedComponents.size() > 0 ? 1 : 0
        );

        List<String> rankedFeedback = feedbackPriorityQueue.rank(buildFeedbackItems(
                matchedComponents,
                missingComponents,
                scalabilityIssues,
                bottlenecks,
                tradeOffFeedback
        ));

        String summaryFeedback = buildSummaryFeedback(
                matchedComponents,
                missingComponents,
                scalabilityIssues,
                bottlenecks,
                tradeOffFeedback,
                score,
                rankedFeedback
        );

        return new SystemDesignEvaluationResult(
                score,
                matchedComponents,
                missingComponents,
                scalabilityIssues,
                bottlenecks,
                tradeOffFeedback,
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

    private boolean containsComponent(Set<String> answerTokens, String component) {
        Set<String> componentTokens = toNormalizedTokenSet(component);
        return answerTokens.containsAll(componentTokens);
    }

    private List<String> detectScalabilityIssues(Set<String> tokens) {
        List<String> issues = new ArrayList<>();

        if (!mentionsAny(tokens, "cache", "caching")) {
            issues.add("The answer does not mention caching, so read-heavy traffic may overload the primary database.");
        }
        if (!mentionsAny(tokens, "load", "balancer", "horizontal", "scaling", "replica")) {
            issues.add("The design does not explain how traffic is distributed or how services scale horizontally.");
        }
        if (!mentionsAny(tokens, "queue", "async", "asynchronous", "worker")) {
            issues.add("Background work handling is unclear. Without queues or async processing, spikes may hit synchronous services directly.");
        }

        return issues;
    }

    private List<String> detectBottlenecks(Set<String> tokens) {
        List<String> bottlenecks = new ArrayList<>();

        if (!mentionsAny(tokens, "database", "db", "shard", "replica")) {
            bottlenecks.add("The storage layer is underspecified, which suggests a single hidden database bottleneck.");
        }
        if (!mentionsAny(tokens, "availability", "failover", "redundancy", "replica")) {
            bottlenecks.add("High availability strategy is missing, so a single-node failure path is likely.");
        }
        if (!mentionsAny(tokens, "rate", "limit", "throttle", "backpressure")) {
            bottlenecks.add("Traffic protection is not discussed, so hot users or bursts may overwhelm the system.");
        }

        return bottlenecks;
    }

    private List<String> detectTradeOffGaps(Set<String> tokens) {
        List<String> tradeOffs = new ArrayList<>();
        if (!mentionsAny(tokens, "trade", "off", "consistency", "latency", "cost", "availability")) {
            tradeOffs.add("The answer does not explicitly discuss trade-offs such as latency vs consistency, cost vs reliability, or complexity vs scalability.");
        }
        return tradeOffs;
    }

    private boolean mentionsAny(Set<String> tokens, String... options) {
        for (String option : options) {
            if (tokens.contains(option)) {
                return true;
            }
        }
        return false;
    }

    private List<RankedFeedback> buildFeedbackItems(
            List<String> matchedComponents,
            List<String> missingComponents,
            List<String> scalabilityIssues,
            List<String> bottlenecks,
            List<String> tradeOffFeedback
    ) {
        List<RankedFeedback> feedbackItems = new ArrayList<>();

        if (!missingComponents.isEmpty()) {
            feedbackItems.add(new RankedFeedback(
                    "Missing expected components: " + String.join(", ", missingComponents),
                    100
            ));
        }

        if (!bottlenecks.isEmpty()) {
            feedbackItems.add(new RankedFeedback(
                    "Potential bottlenecks: " + String.join(" | ", bottlenecks),
                    90
            ));
        }

        if (!scalabilityIssues.isEmpty()) {
            feedbackItems.add(new RankedFeedback(
                    "Scalability issues: " + String.join(" | ", scalabilityIssues),
                    80
            ));
        }

        if (!tradeOffFeedback.isEmpty()) {
            feedbackItems.add(new RankedFeedback(
                    "Trade-off discussion is weak: " + String.join(" | ", tradeOffFeedback),
                    70
            ));
        }

        if (!matchedComponents.isEmpty()) {
            feedbackItems.add(new RankedFeedback(
                    "Matched components: " + String.join(", ", matchedComponents),
                    40
            ));
        }

        return feedbackItems;
    }

    private String buildSummaryFeedback(
            List<String> matchedComponents,
            List<String> missingComponents,
            List<String> scalabilityIssues,
            List<String> bottlenecks,
            List<String> tradeOffFeedback,
            int score,
            List<String> rankedFeedback
    ) {
        StringBuilder feedback = new StringBuilder();
        feedback.append("Matched components: ")
                .append(matchedComponents.isEmpty() ? "none" : String.join(", ", matchedComponents))
                .append(". ");

        if (!missingComponents.isEmpty()) {
            feedback.append("Missing components: ")
                    .append(String.join(", ", missingComponents))
                    .append(". ");
        }

        if (!scalabilityIssues.isEmpty()) {
            feedback.append("Scalability issues identified. ");
        }
        if (!bottlenecks.isEmpty()) {
            feedback.append("Potential bottlenecks identified. ");
        }
        if (!tradeOffFeedback.isEmpty()) {
            feedback.append("Trade-off discussion is incomplete. ");
        }

        feedback.append("Final score: ")
                .append(score)
                .append("%. ");

        if (!rankedFeedback.isEmpty()) {
            feedback.append("Top feedback: ")
                    .append(rankedFeedback.get(0))
                    .append(".");
        }

        return feedback.toString();
    }
}
