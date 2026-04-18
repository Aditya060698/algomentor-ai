package com.algomentorai.backend.service;

import com.algomentorai.backend.common.exception.ResourceNotFoundException;
import com.algomentorai.backend.dto.AttemptResponse;
import com.algomentorai.backend.dto.SubmitAttemptRequest;
import com.algomentorai.backend.entity.Attempt;
import com.algomentorai.backend.entity.AttemptStatus;
import com.algomentorai.backend.entity.Question;
import com.algomentorai.backend.entity.QuestionType;
import com.algomentorai.backend.entity.User;
import com.algomentorai.backend.evaluation.dto.DsaEvaluationResult;
import com.algomentorai.backend.evaluation.dto.SystemDesignEvaluationResult;
import com.algomentorai.backend.evaluation.service.DsaEvaluationService;
import com.algomentorai.backend.evaluation.service.SystemDesignEvaluationService;
import com.algomentorai.backend.repository.AttemptRepository;
import com.algomentorai.backend.repository.QuestionRepository;
import com.algomentorai.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AttemptService {

    private final AttemptRepository attemptRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final DsaEvaluationService dsaEvaluationService;
    private final SystemDesignEvaluationService systemDesignEvaluationService;

    public AttemptService(
            AttemptRepository attemptRepository,
            UserRepository userRepository,
            QuestionRepository questionRepository,
            DsaEvaluationService dsaEvaluationService,
            SystemDesignEvaluationService systemDesignEvaluationService
    ) {
        this.attemptRepository = attemptRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.dsaEvaluationService = dsaEvaluationService;
        this.systemDesignEvaluationService = systemDesignEvaluationService;
    }

    @Transactional
    public AttemptResponse submitAttempt(SubmitAttemptRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found for id " + request.userId()));

        Question question = questionRepository.findById(request.questionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found for id " + request.questionId()));

        EvaluationResult evaluationResult = question.getType() == QuestionType.DSA
                ? evaluateDsaAttempt(request.submittedAnswer(), question)
                : evaluateSystemDesignAttempt(request.submittedAnswer(), question);

        Attempt savedAttempt = attemptRepository.save(Attempt.builder()
                .user(user)
                .question(question)
                .submittedAnswer(request.submittedAnswer())
                .score(evaluationResult.score())
                .automatedFeedback(evaluationResult.feedback())
                .status(AttemptStatus.REVIEWED)
                .build());

        return new AttemptResponse(
                savedAttempt.getId(),
                savedAttempt.getUser().getId(),
                savedAttempt.getQuestion().getId(),
                savedAttempt.getScore(),
                savedAttempt.getAutomatedFeedback(),
                savedAttempt.getStatus(),
                savedAttempt.getCreatedAt()
        );
    }

    private EvaluationResult evaluateDsaAttempt(String submittedAnswer, Question question) {
        DsaEvaluationResult result = dsaEvaluationService.evaluate(
                submittedAnswer,
                question.getExpectedConcepts(),
                question.getSampleAnswer()
        );

        return new EvaluationResult(result.score(), result.summaryFeedback());
    }

    private EvaluationResult evaluateSystemDesignAttempt(String submittedAnswer, Question question) {
        SystemDesignEvaluationResult result = systemDesignEvaluationService.evaluate(
                submittedAnswer,
                question.getExpectedConcepts()
        );

        return new EvaluationResult(result.score(), result.summaryFeedback());
    }

    private record EvaluationResult(int score, String feedback) {
    }
}
