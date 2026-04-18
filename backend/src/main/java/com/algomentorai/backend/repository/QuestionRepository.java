package com.algomentorai.backend.repository;

import com.algomentorai.backend.entity.DifficultyLevel;
import com.algomentorai.backend.entity.Question;
import com.algomentorai.backend.entity.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByType(QuestionType type);

    List<Question> findByDifficulty(DifficultyLevel difficulty);

    List<Question> findByTypeAndDifficulty(QuestionType type, DifficultyLevel difficulty);
}
