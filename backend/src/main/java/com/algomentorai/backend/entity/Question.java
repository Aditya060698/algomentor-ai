package com.algomentorai.backend.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
public class Question extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private QuestionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DifficultyLevel difficulty;

    // These concepts work like a small rubric. The evaluation layer can check
    // whether the learner mentioned the key ideas expected for a strong answer.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_expected_concepts", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "concept", nullable = false, length = 120)
    private List<String> expectedConcepts = new ArrayList<>();

    // The sample answer is stored for internal evaluation, review workflows,
    // and future mentor/admin tooling. It should not be exposed in learner APIs.
    @Lob
    @Column(nullable = false)
    private String sampleAnswer;

    public static Builder builder() {
        return new Builder();
    }

    public String getTitle() {
        return title;
    }

    public QuestionType getType() {
        return type;
    }

    public DifficultyLevel getDifficulty() {
        return difficulty;
    }

    public List<String> getExpectedConcepts() {
        return expectedConcepts;
    }

    public String getSampleAnswer() {
        return sampleAnswer;
    }

    public static class Builder {
        private final Question question = new Question();

        public Builder title(String title) {
            question.title = title;
            return this;
        }

        public Builder type(QuestionType type) {
            question.type = type;
            return this;
        }

        public Builder difficulty(DifficultyLevel difficulty) {
            question.difficulty = difficulty;
            return this;
        }

        public Builder expectedConcepts(List<String> expectedConcepts) {
            question.expectedConcepts = new ArrayList<>(expectedConcepts);
            return this;
        }

        public Builder sampleAnswer(String sampleAnswer) {
            question.sampleAnswer = sampleAnswer;
            return this;
        }

        public Question build() {
            return question;
        }
    }
}
