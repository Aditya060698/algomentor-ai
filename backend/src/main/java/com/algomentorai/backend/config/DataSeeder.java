package com.algomentorai.backend.config;

import com.algomentorai.backend.entity.DifficultyLevel;
import com.algomentorai.backend.entity.Question;
import com.algomentorai.backend.entity.QuestionType;
import com.algomentorai.backend.entity.User;
import com.algomentorai.backend.rag.entity.ConceptNote;
import com.algomentorai.backend.rag.entity.ConceptNoteType;
import com.algomentorai.backend.rag.repository.ConceptNoteRepository;
import com.algomentorai.backend.rag.service.EmbeddingService;
import com.algomentorai.backend.repository.QuestionRepository;
import com.algomentorai.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedInitialData(
            UserRepository userRepository,
            QuestionRepository questionRepository,
            ConceptNoteRepository conceptNoteRepository,
            EmbeddingService embeddingService
    ) {
        return args -> {
            if (userRepository.count() == 0) {
                userRepository.save(User.builder()
                        .username("alice")
                        .email("alice@algomentor.dev")
                        .build());

                userRepository.save(User.builder()
                        .username("bob")
                        .email("bob@algomentor.dev")
                        .build());
            }

            if (questionRepository.count() == 0) {
                questionRepository.save(Question.builder()
                        .title("Two Sum")
                        .type(QuestionType.DSA)
                        .difficulty(DifficultyLevel.EASY)
                        .expectedConcepts(List.of("hash map", "complement lookup", "one-pass traversal", "time complexity"))
                        .sampleAnswer("Use a hash map while iterating once through the array. For each number, compute the complement and check whether it already exists in the map. If yes, return the stored index and current index. This gives O(n) time complexity and O(n) space complexity.")
                        .build());

                questionRepository.save(Question.builder()
                        .title("Design a URL Shortener")
                        .type(QuestionType.SYSTEM_DESIGN)
                        .difficulty(DifficultyLevel.MEDIUM)
                        .expectedConcepts(List.of("API design", "key generation", "database", "cache", "scalability", "availability"))
                        .sampleAnswer("A strong design should define create and redirect APIs, a unique short-code generation strategy, persistent storage for URL mappings, cache for hot links, and horizontal scaling for redirect traffic. It should also discuss availability, analytics, and failure handling.")
                        .build());
            }

            if (conceptNoteRepository.count() == 0) {
                conceptNoteRepository.save(buildConceptNote(
                        embeddingService,
                        "Hash Map Pattern",
                        ConceptNoteType.DSA_PATTERN,
                        "Use a hash map when you need constant-time lookup for previously seen values. This pattern is common in complement lookup, frequency counting, deduplication, and prefix aggregation problems.",
                        List.of("hash map", "lookup", "complement", "frequency counting", "arrays")
                ));

                conceptNoteRepository.save(buildConceptNote(
                        embeddingService,
                        "Two Pointers Pattern",
                        ConceptNoteType.DSA_PATTERN,
                        "Two pointers are useful when processing sorted arrays, linked lists, or sliding windows. The idea is to move one or both pointers based on a comparison condition to reduce brute-force search.",
                        List.of("two pointers", "sorted array", "sliding window", "linked list")
                ));

                conceptNoteRepository.save(buildConceptNote(
                        embeddingService,
                        "Caching In System Design",
                        ConceptNoteType.SYSTEM_DESIGN_TOPIC,
                        "Caching reduces latency and database load by storing frequently accessed data closer to the application. Common considerations include eviction policy, cache invalidation, read-through versus write-through, and consistency trade-offs.",
                        List.of("cache", "latency", "eviction", "consistency", "scalability")
                ));

                conceptNoteRepository.save(buildConceptNote(
                        embeddingService,
                        "Load Balancing",
                        ConceptNoteType.SYSTEM_DESIGN_TOPIC,
                        "Load balancers distribute incoming traffic across multiple service instances to improve availability, throughput, and fault tolerance. They can operate at Layer 4 or Layer 7 depending on routing needs.",
                        List.of("load balancer", "availability", "throughput", "fault tolerance", "scaling")
                ));
            }
        };
    }

    private ConceptNote buildConceptNote(
            EmbeddingService embeddingService,
            String title,
            ConceptNoteType type,
            String content,
            List<String> keywords
    ) {
        String combinedText = title + " " + content + " " + String.join(" ", keywords);
        return ConceptNote.builder()
                .title(title)
                .type(type)
                .content(content)
                .keywords(keywords)
                .embedding(embeddingService.createEmbedding(combinedText))
                .build();
    }
}
