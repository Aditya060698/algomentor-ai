package com.algomentorai.backend.repository;

import com.algomentorai.backend.entity.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttemptRepository extends JpaRepository<Attempt, Long> {
}
