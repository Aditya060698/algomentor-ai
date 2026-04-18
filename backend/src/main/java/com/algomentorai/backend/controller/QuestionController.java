package com.algomentorai.backend.controller;

import com.algomentorai.backend.common.api.ApiResponse;
import com.algomentorai.backend.dto.QuestionResponse;
import com.algomentorai.backend.entity.DifficultyLevel;
import com.algomentorai.backend.entity.QuestionType;
import com.algomentorai.backend.service.QuestionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public ApiResponse<List<QuestionResponse>> getQuestions(
            @RequestParam(required = false) QuestionType type,
            @RequestParam(required = false) DifficultyLevel difficulty
    ) {
        return ApiResponse.success(
                "Questions fetched successfully",
                questionService.getQuestions(type, difficulty)
        );
    }
}
