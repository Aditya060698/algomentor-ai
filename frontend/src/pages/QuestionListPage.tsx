import { useEffect, useMemo, useState } from "react";
import { getQuestions } from "../api/client";
import { QuestionCard } from "../components/QuestionCard";
import type { DifficultyLevel, Question, QuestionType } from "../types/api";

export function QuestionListPage() {
  const [questions, setQuestions] = useState<Question[]>([]);
  const [typeFilter, setTypeFilter] = useState<QuestionType | "ALL">("ALL");
  const [difficultyFilter, setDifficultyFilter] = useState<DifficultyLevel | "ALL">("ALL");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function loadQuestions() {
      try {
        setLoading(true);
        const data = await getQuestions({
          type: typeFilter === "ALL" ? undefined : typeFilter,
          difficulty: difficultyFilter === "ALL" ? undefined : difficultyFilter
        });
        setQuestions(data);
        setError(null);
      } catch (loadError) {
        setError(loadError instanceof Error ? loadError.message : "Failed to load questions");
      } finally {
        setLoading(false);
      }
    }

    void loadQuestions();
  }, [typeFilter, difficultyFilter]);

  const heading = useMemo(() => {
    if (typeFilter === "ALL" && difficultyFilter === "ALL") {
      return "All practice questions";
    }

    return `Questions filtered by ${typeFilter === "ALL" ? "all types" : typeFilter} and ${difficultyFilter === "ALL" ? "all difficulties" : difficultyFilter}`;
  }, [typeFilter, difficultyFilter]);

  return (
    <section className="page-stack">
      <div className="hero card">
        <p className="eyebrow">Question Engine</p>
        <h1>{heading}</h1>
        <p className="muted">Use filters to move between DSA and System Design practice without changing the page structure.</p>
      </div>

      <section className="filters card">
        <label>
          Type
          <select value={typeFilter} onChange={(event) => setTypeFilter(event.target.value as QuestionType | "ALL") }>
            <option value="ALL">All</option>
            <option value="DSA">DSA</option>
            <option value="SYSTEM_DESIGN">System Design</option>
          </select>
        </label>

        <label>
          Difficulty
          <select value={difficultyFilter} onChange={(event) => setDifficultyFilter(event.target.value as DifficultyLevel | "ALL") }>
            <option value="ALL">All</option>
            <option value="EASY">Easy</option>
            <option value="MEDIUM">Medium</option>
            <option value="HARD">Hard</option>
          </select>
        </label>
      </section>

      {loading ? <div className="card">Loading questions...</div> : null}
      {error ? <div className="card error">{error}</div> : null}

      <section className="question-grid">
        {questions.map((question) => (
          <QuestionCard key={question.id} question={question} />
        ))}
      </section>
    </section>
  );
}
