import { Link } from "react-router-dom";
import type { Question } from "../types/api";

interface QuestionCardProps {
  question: Question;
}

export function QuestionCard({ question }: QuestionCardProps) {
  return (
    <article className="card question-card">
      <div className="question-meta-row">
        <span className="badge">{question.type}</span>
        <span className="badge subtle">{question.difficulty}</span>
      </div>

      <h3>{question.title}</h3>
      <p className="muted">Expected concepts: {question.expectedConcepts.join(", ")}</p>

      <div className="card-actions">
        <Link className="button secondary" to={`/practice/${question.id}`}>
          Practice
        </Link>
        <Link className="button" to={`/submit/${question.id}`}>
          Submit Answer
        </Link>
      </div>
    </article>
  );
}
