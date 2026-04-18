import { Link, useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { getQuestionById } from "../api/client";
import type { Question } from "../types/api";

export function PracticePage() {
  const { questionId } = useParams();
  const [question, setQuestion] = useState<Question | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function loadQuestion() {
      if (!questionId) {
        setError("Question id is missing");
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        const data = await getQuestionById(Number(questionId));
        setQuestion(data);
        setError(null);
      } catch (loadError) {
        setError(loadError instanceof Error ? loadError.message : "Failed to load question");
      } finally {
        setLoading(false);
      }
    }

    void loadQuestion();
  }, [questionId]);

  if (loading) {
    return <div className="card">Loading practice content...</div>;
  }

  if (error || !question) {
    return <div className="card error">{error ?? "Question not found"}</div>;
  }

  return (
    <section className="page-stack">
      <article className="card">
        <p className="eyebrow">Practice Page</p>
        <h1>{question.title}</h1>
        <div className="question-meta-row">
          <span className="badge">{question.type}</span>
          <span className="badge subtle">{question.difficulty}</span>
        </div>
        <p className="muted">This page is where the learner studies the prompt, identifies the pattern, and prepares an answer before submission.</p>
      </article>

      <article className="card">
        <h2>How to approach this question</h2>
        <ul className="plain-list">
          <li>Restate the problem in your own words.</li>
          <li>List candidate patterns from the expected concepts.</li>
          <li>Decide on complexity targets before writing the answer.</li>
        </ul>
      </article>

      <article className="card">
        <h2>Expected Concepts</h2>
        <div className="chip-row">
          {question.expectedConcepts.map((concept) => (
            <span className="chip" key={concept}>{concept}</span>
          ))}
        </div>
      </article>

      <div className="card-actions">
        <Link className="button secondary" to="/questions">Back to Questions</Link>
        <Link className="button" to={`/submit/${question.id}`}>Write Answer</Link>
      </div>
    </section>
  );
}
