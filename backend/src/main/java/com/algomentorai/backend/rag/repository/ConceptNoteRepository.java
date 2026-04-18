package com.algomentorai.backend.rag.repository;

import com.algomentorai.backend.rag.entity.ConceptNote;
import com.algomentorai.backend.rag.entity.ConceptNoteType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConceptNoteRepository extends JpaRepository<ConceptNote, Long> {

    List<ConceptNote> findByType(ConceptNoteType type);
}
