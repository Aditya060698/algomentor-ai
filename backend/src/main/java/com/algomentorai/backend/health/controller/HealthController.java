package com.algomentorai.backend.health.controller;

import com.algomentorai.backend.common.api.ApiResponse;
import com.algomentorai.backend.health.dto.HealthStatusResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    @GetMapping
    public ApiResponse<HealthStatusResponse> health() {
        return ApiResponse.success("Service is healthy", new HealthStatusResponse("algomentor-ai-backend", "UP"));
    }
}
