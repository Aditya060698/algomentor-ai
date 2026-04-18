package com.algomentorai.backend.dsa.heap;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

@Component
public class FeedbackPriorityQueue {

    public List<String> rank(List<RankedFeedback> feedbackItems) {
        PriorityQueue<RankedFeedback> queue = new PriorityQueue<>(Comparator.comparingInt(RankedFeedback::priority).reversed());
        queue.addAll(feedbackItems);

        List<String> orderedFeedback = new ArrayList<>();
        while (!queue.isEmpty()) {
            orderedFeedback.add(queue.poll().message());
        }
        return orderedFeedback;
    }
}
