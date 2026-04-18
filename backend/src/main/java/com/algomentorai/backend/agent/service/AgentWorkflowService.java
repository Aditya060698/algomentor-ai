package com.algomentorai.backend.agent.service;

import com.algomentorai.backend.agent.dto.AgentQuestionType;
import com.algomentorai.backend.agent.dto.AgentResponse;
import com.algomentorai.backend.agent.dto.RetrievedConcept;
import com.algomentorai.backend.rag.dto.ConceptSearchResponse;
import com.algomentorai.backend.rag.entity.ConceptNoteType;
import com.algomentorai.backend.rag.service.RetrievalService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class AgentWorkflowService {

    private final RetrievalService retrievalService;

    public AgentWorkflowService(RetrievalService retrievalService) {
        this.retrievalService = retrievalService;
    }

    public AgentResponse processQuery(String query) {
        AgentQuestionType classifiedType = classify(query);
        List<RetrievedConcept> retrievedConcepts = retrieveConcepts(query, classifiedType);

        String explanation = generateExplanation(query, classifiedType, retrievedConcepts);
        String approach = generateApproach(classifiedType, retrievedConcepts);
        String code = generateCode(classifiedType, query, retrievedConcepts);
        List<String> edgeCases = generateEdgeCases(classifiedType, query);
        List<String> followUpQuestions = generateFollowUpQuestions(classifiedType);

        return new AgentResponse(
                classifiedType,
                explanation,
                approach,
                code,
                edgeCases,
                followUpQuestions,
                retrievedConcepts
        );
    }

    private AgentQuestionType classify(String query) {
        String normalizedQuery = query.toLowerCase(Locale.ROOT);

        if (containsAny(normalizedQuery,
                "design", "scalable", "service", "architecture", "throughput",
                "availability", "distributed", "database", "load balancer", "cache", "api"
        )) {
            return AgentQuestionType.SYSTEM_DESIGN;
        }

        return AgentQuestionType.DSA;
    }

    private List<RetrievedConcept> retrieveConcepts(String query, AgentQuestionType classifiedType) {
        ConceptNoteType conceptType = classifiedType == AgentQuestionType.DSA
                ? ConceptNoteType.DSA_PATTERN
                : ConceptNoteType.SYSTEM_DESIGN_TOPIC;

        return retrievalService.retrieveTopConcepts(query, 3, conceptType)
                .stream()
                .map(concept -> new RetrievedConcept(
                        concept.title(),
                        concept.content(),
                        concept.keywords(),
                        concept.similarityScore()
                ))
                .toList();
    }

    private String generateExplanation(String query, AgentQuestionType classifiedType, List<RetrievedConcept> retrievedConcepts) {
        String supportingConcepts = retrievedConcepts.stream()
                .map(RetrievedConcept::title)
                .reduce((left, right) -> left + ", " + right)
                .orElse("core fundamentals");

        if (classifiedType == AgentQuestionType.DSA) {
            return "This looks like a DSA problem. The goal is to identify the underlying pattern instead of jumping directly into code. Based on the query, the most relevant supporting concepts are "
                    + supportingConcepts
                    + ". These concepts help reduce brute-force reasoning and guide you toward an efficient solution.";
        }

        return "This looks like a System Design problem. The goal is to break the system into clear responsibilities, constraints, and trade-offs before discussing components. Based on the query, the most relevant supporting concepts are "
                + supportingConcepts
                + ". These concepts anchor the design discussion around scale, reliability, and maintainability.";
    }

    private String generateApproach(AgentQuestionType classifiedType, List<RetrievedConcept> retrievedConcepts) {
        String conceptSummary = retrievedConcepts.stream()
                .map(concept -> "- " + concept.title() + ": " + concept.content())
                .reduce((left, right) -> left + "\n" + right)
                .orElse("- No supporting concepts were retrieved.");

        if (classifiedType == AgentQuestionType.DSA) {
            return "1. Restate the problem in terms of inputs, outputs, and constraints.\n"
                    + "2. Identify the most likely algorithmic pattern from the retrieved concepts.\n"
                    + "3. Choose a data structure that reduces repeated work.\n"
                    + "4. Validate time and space complexity before coding.\n"
                    + "Relevant retrieved concepts:\n"
                    + conceptSummary;
        }

        return "1. Clarify functional requirements and non-functional requirements.\n"
                + "2. Define APIs, core data model, and traffic flow.\n"
                + "3. Introduce storage, caching, scaling, and failure-handling components.\n"
                + "4. Discuss trade-offs explicitly instead of naming components without justification.\n"
                + "Relevant retrieved concepts:\n"
                + conceptSummary;
    }

    private String generateCode(AgentQuestionType classifiedType, String query, List<RetrievedConcept> retrievedConcepts) {
        if (classifiedType == AgentQuestionType.SYSTEM_DESIGN) {
            return null;
        }

        String normalizedQuery = query.toLowerCase(Locale.ROOT);
        boolean usesHashMap = retrievedConcepts.stream().anyMatch(concept -> concept.title().toLowerCase(Locale.ROOT).contains("hash map"))
                || normalizedQuery.contains("two sum")
                || normalizedQuery.contains("complement");

        if (usesHashMap) {
            return "public int[] solve(int[] nums, int target) {\n"
                    + "    Map<Integer, Integer> seen = new HashMap<>();\n"
                    + "\n"
                    + "    for (int i = 0; i < nums.length; i++) {\n"
                    + "        int complement = target - nums[i];\n"
                    + "        if (seen.containsKey(complement)) {\n"
                    + "            return new int[]{seen.get(complement), i};\n"
                    + "        }\n"
                    + "        seen.put(nums[i], i);\n"
                    + "    }\n"
                    + "\n"
                    + "    return new int[]{-1, -1};\n"
                    + "}";
        }

        return "public void solve() {\n"
                + "    // 1. Understand constraints and identify the pattern.\n"
                + "    // 2. Select the right data structure.\n"
                + "    // 3. Implement the optimized approach.\n"
                + "    // 4. Test edge cases before finalizing.\n"
                + "}";
    }

    private List<String> generateEdgeCases(AgentQuestionType classifiedType, String query) {
        if (classifiedType == AgentQuestionType.DSA) {
            return List.of(
                    "Empty input or minimal-size input.",
                    "Duplicate values that may affect indexing or matching.",
                    "Negative numbers, zeros, or large values that affect arithmetic logic.",
                    "Cases where no valid answer exists and the function must handle failure cleanly."
            );
        }

        return List.of(
                "Traffic spikes that exceed a single-node design.",
                "Cache misses, stale cache entries, or cache invalidation issues.",
                "Database or downstream service failures that require graceful degradation.",
                "Hot keys or uneven traffic distribution causing bottlenecks."
        );
    }

    private List<String> generateFollowUpQuestions(AgentQuestionType classifiedType) {
        if (classifiedType == AgentQuestionType.DSA) {
            return List.of(
                    "What is the time complexity of your approach, and can it be improved?",
                    "Why is this data structure a better choice than a brute-force approach?",
                    "How would you modify the solution if the input size became very large?"
            );
        }

        return List.of(
                "What are the main bottlenecks in this design under high traffic?",
                "How would you improve availability if one component fails?",
                "What trade-offs did you make between consistency, latency, and cost?"
        );
    }

    private boolean containsAny(String content, String... keywords) {
        for (String keyword : keywords) {
            if (content.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
