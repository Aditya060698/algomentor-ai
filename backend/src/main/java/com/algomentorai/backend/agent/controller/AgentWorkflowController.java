package com.algomentorai.backend.agent.controller;

import com.algomentorai.backend.agent.dto.AgentRequest;
import com.algomentorai.backend.agent.dto.AgentResponse;
import com.algomentorai.backend.agent.service.AgentWorkflowService;
import com.algomentorai.backend.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/agent")
public class AgentWorkflowController {

    private final AgentWorkflowService agentWorkflowService;

    public AgentWorkflowController(AgentWorkflowService agentWorkflowService) {
        this.agentWorkflowService = agentWorkflowService;
    }

    @PostMapping("/respond")
    public ApiResponse<AgentResponse> respond(@Valid @RequestBody AgentRequest request) {
        return ApiResponse.success(
                "Structured agent response generated successfully",
                agentWorkflowService.processQuery(request.query())
        );
    }
}
