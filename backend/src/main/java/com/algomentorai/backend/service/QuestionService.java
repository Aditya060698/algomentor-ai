package com.algomentorai.backend.service;

import com.algomentorai.backend.dto.QuestionResponse;
import com.algomentorai.backend.entity.DifficultyLevel;
import com.algomentorai.backend.entity.Question;
import com.algomentorai.backend.entity.QuestionType;
import com.algomentorai.backend.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<QuestionResponse> getQuestions(QuestionType type, DifficultyLevel difficulty) {
        List<Question> questions;

        if (type != null && difficulty != null) {
            questions = questionRepository.findByTypeAndDifficulty(type, difficulty);
        } else if (type != null) {
            questions = questionRepository.findByType(type);
        } else if (difficulty != null) {
            questions = questionRepository.findByDifficulty(difficulty);
        } else {
            questions = questionRepository.findAll();
        }

        return questions.stream()
                .map(this::toResponse)
                .toList();
    }

    private QuestionResponse toResponse(Question question) {
        return new QuestionResponse(
                question.getId(),
                question.getTitle(),
                question.getType(),
                question.getDifficulty(),
                question.getExpectedConcepts(),
                question.getCreatedAt()
        );
    }
}
