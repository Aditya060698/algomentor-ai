import { Link } from "react-router-dom";
import { FeedbackPanel } from "../components/FeedbackPanel";
import type { AttemptResponse } from "../types/api";

const FEEDBACK_STORAGE_KEY = "algomentor.latestFeedback";

export function FeedbackPage() {
  const raw = sessionStorage.getItem(FEEDBACK_STORAGE_KEY);
  const feedback = raw ? (JSON.parse(raw) as AttemptResponse) : null;

  return (
    <section className="page-stack">
      <article className="card">
        <p className="eyebrow">Feedback Display</p>
        <h1>Evaluation Result</h1>
        <p className="muted">This page reads the latest attempt result from session storage after the submission API completes.</p>
      </article>

      {feedback ? <FeedbackPanel result={feedback} /> : <div className="card error">No feedback found. Submit an answer first.</div>}

      <div className="card-actions">
        <Link className="button secondary" to="/questions">Back to Questions</Link>
      </div>
    </section>
  );
}
