package com.algomentorai.backend.controller;

import com.algomentorai.backend.common.api.ApiResponse;
import com.algomentorai.backend.dto.AttemptResponse;
import com.algomentorai.backend.dto.SubmitAttemptRequest;
import com.algomentorai.backend.service.AttemptService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/attempts")
public class AttemptController {

    private final AttemptService attemptService;

    public AttemptController(AttemptService attemptService) {
        this.attemptService = attemptService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AttemptResponse> submitAttempt(@Valid @RequestBody SubmitAttemptRequest request) {
        return ApiResponse.success("Attempt submitted successfully", attemptService.submitAttempt(request));
    }
}
