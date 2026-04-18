package com.algomentorai.backend.rag.controller;

import com.algomentorai.backend.common.api.ApiResponse;
import com.algomentorai.backend.rag.dto.ConceptSearchResponse;
import com.algomentorai.backend.rag.entity.ConceptNoteType;
import com.algomentorai.backend.rag.service.RetrievalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rag")
public class RagController {

    private final RetrievalService retrievalService;

    public RagController(RetrievalService retrievalService) {
        this.retrievalService = retrievalService;
    }

    @GetMapping("/concepts/search")
    public ApiResponse<List<ConceptSearchResponse>> searchConcepts(
            @RequestParam String query,
            @RequestParam(defaultValue = "3") Integer topK,
            @RequestParam(required = false) ConceptNoteType type
    ) {
        return ApiResponse.success(
                "Relevant concept notes fetched successfully",
                retrievalService.retrieveTopConcepts(query, topK, type)
        );
    }
}
