package com.algomentorai.backend.dsa.trie;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Component
public class PatternTrie {

    private final TrieNode root = new TrieNode();

    public PatternTrie() {
        insert("sliding window");
        insert("dynamic programming");
        insert("dp");
        insert("graph");
        insert("bfs");
        insert("dfs");
        insert("hash map");
        insert("two pointers");
    }

    public void insert(String phrase) {
        TrieNode current = root;
        String normalized = normalize(phrase);

        for (char character : normalized.toCharArray()) {
            current = current.getChildren().computeIfAbsent(character, ignored -> new TrieNode());
        }

        current.setTerminal(true);
        current.setPatternName(phrase);
    }

    public Set<String> detectPatterns(String text) {
        Set<String> detectedPatterns = new HashSet<>();
        String normalized = normalize(text);

        for (int index = 0; index < normalized.length(); index++) {
            TrieNode current = root;
            int cursor = index;

            while (cursor < normalized.length()) {
                char character = normalized.charAt(cursor);
                current = current.getChildren().get(character);
                if (current == null) {
                    break;
                }

                if (current.isTerminal()) {
                    detectedPatterns.add(current.getPatternName());
                }
                cursor++;
            }
        }

        return detectedPatterns;
    }

    private String normalize(String content) {
        return content.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
    }
}
