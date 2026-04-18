package com.algomentorai.backend.rag.service;

import com.algomentorai.backend.rag.dto.ConceptSearchResponse;
import com.algomentorai.backend.rag.entity.ConceptNote;
import com.algomentorai.backend.rag.entity.ConceptNoteType;
import com.algomentorai.backend.rag.repository.ConceptNoteRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class RetrievalService {

    private final ConceptNoteRepository conceptNoteRepository;
    private final EmbeddingService embeddingService;

    public RetrievalService(ConceptNoteRepository conceptNoteRepository, EmbeddingService embeddingService) {
        this.conceptNoteRepository = conceptNoteRepository;
        this.embeddingService = embeddingService;
    }

    public List<ConceptSearchResponse> retrieveTopConcepts(String query, Integer topK, ConceptNoteType type) {
        String queryEmbedding = embeddingService.createEmbedding(query);
        int limit = topK == null ? 3 : topK;

        List<ConceptNote> conceptNotes = type == null
                ? conceptNoteRepository.findAll()
                : conceptNoteRepository.findByType(type);

        return conceptNotes.stream()
                .map(conceptNote -> toResponse(conceptNote, queryEmbedding))
                .sorted(Comparator.comparingDouble(ConceptSearchResponse::similarityScore).reversed())
                .limit(limit)
                .toList();
    }

    private ConceptSearchResponse toResponse(ConceptNote conceptNote, String queryEmbedding) {
        double similarityScore = embeddingService.cosineSimilarity(queryEmbedding, conceptNote.getEmbedding());

        return new ConceptSearchResponse(
                conceptNote.getId(),
                conceptNote.getTitle(),
                conceptNote.getType(),
                conceptNote.getContent(),
                conceptNote.getKeywords(),
                similarityScore
        );
    }
}
