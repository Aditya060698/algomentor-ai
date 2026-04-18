import type { AttemptResponse } from "../types/api";

interface FeedbackPanelProps {
  result: AttemptResponse;
}

export function FeedbackPanel({ result }: FeedbackPanelProps) {
  return (
    <section className="card feedback-panel">
      <div className="feedback-score">Score: {result.score}</div>
      <div className="feedback-status">Status: {result.status}</div>
      <p>{result.automatedFeedback}</p>
      <p className="muted">Submitted at: {new Date(result.createdAt).toLocaleString()}</p>
    </section>
  );
}
