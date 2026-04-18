package com.algomentorai.backend.dsa.scoring;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ScoringEngine {

    public int calculateScore(int expectedConceptCount, int matchedConceptCount, boolean complexityMissing, boolean complexityIncorrect, int detectedPatternCount) {
        Map<String, Integer> scoreCard = new HashMap<>();

        int conceptScore = expectedConceptCount == 0
                ? 0
                : (int) Math.round((matchedConceptCount * 70.0) / expectedConceptCount);

        scoreCard.put("conceptCoverage", conceptScore);
        scoreCard.put("patternRecognition", Math.min(detectedPatternCount * 10, 20));
        scoreCard.put("complexityPresence", complexityMissing ? 0 : 10);
        scoreCard.put("complexityPenalty", complexityIncorrect ? -15 : 0);

        int total = 0;
        for (int value : scoreCard.values()) {
            total += value;
        }

        return Math.max(0, Math.min(total, 100));
    }
}
