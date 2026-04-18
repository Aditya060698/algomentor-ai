package com.algomentorai.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "attempts")
public class Attempt extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false, length = 4000)
    private String submittedAnswer;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false, length = 2000)
    private String automatedFeedback;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttemptStatus status;

    public static Builder builder() {
        return new Builder();
    }

    public User getUser() {
        return user;
    }

    public Question getQuestion() {
        return question;
    }

    public String getSubmittedAnswer() {
        return submittedAnswer;
    }

    public Integer getScore() {
        return score;
    }

    public String getAutomatedFeedback() {
        return automatedFeedback;
    }

    public AttemptStatus getStatus() {
        return status;
    }

    public static class Builder {
        private final Attempt attempt = new Attempt();

        public Builder user(User user) {
            attempt.user = user;
            return this;
        }

        public Builder question(Question question) {
            attempt.question = question;
            return this;
        }

        public Builder submittedAnswer(String submittedAnswer) {
            attempt.submittedAnswer = submittedAnswer;
            return this;
        }

        public Builder score(Integer score) {
            attempt.score = score;
            return this;
        }

        public Builder automatedFeedback(String automatedFeedback) {
            attempt.automatedFeedback = automatedFeedback;
            return this;
        }

        public Builder status(AttemptStatus status) {
            attempt.status = status;
            return this;
        }

        public Attempt build() {
            return attempt;
        }
    }
}
