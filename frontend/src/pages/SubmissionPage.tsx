import { FormEvent, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { getQuestionById, submitAttempt } from "../api/client";
import type { Question } from "../types/api";

const DEMO_USER_ID = 1;
const FEEDBACK_STORAGE_KEY = "algomentor.latestFeedback";

export function SubmissionPage() {
  const { questionId } = useParams();
  const navigate = useNavigate();
  const [question, setQuestion] = useState<Question | null>(null);
  const [answer, setAnswer] = useState("");
  const [submitting, setSubmitting] = useState(false);
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

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!question) {
      return;
    }

    try {
      setSubmitting(true);
      const result = await submitAttempt({
        userId: DEMO_USER_ID,
        questionId: question.id,
        submittedAnswer: answer
      });

      sessionStorage.setItem(FEEDBACK_STORAGE_KEY, JSON.stringify(result));
      navigate("/feedback");
    } catch (submitError) {
      setError(submitError instanceof Error ? submitError.message : "Failed to submit answer");
    } finally {
      setSubmitting(false);
    }
  }

  if (loading) {
    return <div className="card">Loading submission form...</div>;
  }

  if (error || !question) {
    return <div className="card error">{error ?? "Question not found"}</div>;
  }

  return (
    <section className="page-stack">
      <article className="card">
        <p className="eyebrow">Answer Submission</p>
        <h1>{question.title}</h1>
        <p className="muted">This page sends the learner's written reasoning to the backend evaluation pipeline.</p>
      </article>

      <form className="card submission-form" onSubmit={handleSubmit}>
        <label>
          Your answer
          <textarea
            rows={14}
            value={answer}
            onChange={(event) => setAnswer(event.target.value)}
            placeholder="Explain your approach, complexity, trade-offs, and edge cases."
            required
          />
        </label>

        <div className="card-actions">
          <button className="button" disabled={submitting} type="submit">
            {submitting ? "Submitting..." : "Submit Answer"}
          </button>
        </div>
      </form>
    </section>
  );
}
