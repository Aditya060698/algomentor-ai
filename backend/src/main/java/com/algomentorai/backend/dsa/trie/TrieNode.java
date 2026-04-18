package com.algomentorai.backend.dsa.trie;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TrieNode {

    private final Map<Character, TrieNode> children = new HashMap<>();
    private boolean terminal;
    private String patternName;

    public Map<Character, TrieNode> getChildren() {
        return children;
    }

    public boolean isTerminal() {
        return terminal;
    }

    public void setTerminal(boolean terminal) {
        this.terminal = terminal;
    }

    public String getPatternName() {
        return patternName;
    }

    public void setPatternName(String patternName) {
        this.patternName = patternName;
    }

    public Collection<TrieNode> childNodes() {
        return children.values();
    }
}
