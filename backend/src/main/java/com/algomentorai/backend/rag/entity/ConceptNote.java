package com.algomentorai.backend.rag.entity;

import com.algomentorai.backend.entity.BaseEntity;
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
@Table(name = "concept_notes")
public class ConceptNote extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ConceptNoteType type;

    @Lob
    @Column(nullable = false)
    private String content;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "concept_note_keywords", joinColumns = @JoinColumn(name = "concept_note_id"))
    @Column(name = "keyword", nullable = false, length = 120)
    private List<String> keywords = new ArrayList<>();

    // In the simple version, the embedding is a serialized sparse vector.
    // In production, this field would usually store a real embedding vector
    // in a vector-capable store or a dedicated retrieval system.
    @Lob
    @Column(nullable = false)
    private String embedding;

    public static Builder builder() {
        return new Builder();
    }

    public String getTitle() {
        return title;
    }

    public ConceptNoteType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public String getEmbedding() {
        return embedding;
    }

    public static class Builder {
        private final ConceptNote conceptNote = new ConceptNote();

        public Builder title(String title) {
            conceptNote.title = title;
            return this;
        }

        public Builder type(ConceptNoteType type) {
            conceptNote.type = type;
            return this;
        }

        public Builder content(String content) {
            conceptNote.content = content;
            return this;
        }

        public Builder keywords(List<String> keywords) {
            conceptNote.keywords = new ArrayList<>(keywords);
            return this;
        }

        public Builder embedding(String embedding) {
            conceptNote.embedding = embedding;
            return this;
        }

        public ConceptNote build() {
            return conceptNote;
        }
    }
}
