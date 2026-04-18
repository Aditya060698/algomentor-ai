package com.algomentorai.backend.rag.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmbeddingService {

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "an", "and", "are", "as", "at", "be", "by", "for", "from",
            "how", "in", "is", "it", "of", "on", "or", "that", "the", "to", "with"
    );

    public String createEmbedding(String text) {
        Map<String, Double> vector = buildVector(text);
        return serialize(vector);
    }

    public double cosineSimilarity(String serializedLeft, String serializedRight) {
        Map<String, Double> left = deserialize(serializedLeft);
        Map<String, Double> right = deserialize(serializedRight);

        double dotProduct = 0.0;
        double leftMagnitude = 0.0;
        double rightMagnitude = 0.0;

        for (Map.Entry<String, Double> entry : left.entrySet()) {
            double leftValue = entry.getValue();
            double rightValue = right.getOrDefault(entry.getKey(), 0.0);
            dotProduct += leftValue * rightValue;
            leftMagnitude += leftValue * leftValue;
        }

        for (double value : right.values()) {
            rightMagnitude += value * value;
        }

        if (leftMagnitude == 0.0 || rightMagnitude == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(leftMagnitude) * Math.sqrt(rightMagnitude));
    }

    private Map<String, Double> buildVector(String text) {
        Map<String, Double> vector = new HashMap<>();

        Arrays.stream(text.toLowerCase(Locale.ROOT).split("\\W+"))
                .map(String::trim)
                .filter(token -> !token.isBlank())
                .filter(token -> token.length() > 2)
                .filter(token -> !STOP_WORDS.contains(token))
                .forEach(token -> vector.merge(token, 1.0, Double::sum));

        return vector;
    }

    private String serialize(Map<String, Double> vector) {
        return vector.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.joining(","));
    }

    private Map<String, Double> deserialize(String serializedVector) {
        Map<String, Double> vector = new HashMap<>();
        if (serializedVector == null || serializedVector.isBlank()) {
            return vector;
        }

        for (String tokenWeight : serializedVector.split(",")) {
            String[] parts = tokenWeight.split(":");
            if (parts.length == 2) {
                vector.put(parts[0], Double.parseDouble(parts[1]));
            }
        }

        return vector;
    }
}
