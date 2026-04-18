import type { ApiResponse, AttemptResponse, DifficultyLevel, Question, QuestionType } from "../types/api";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/api/v1";

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(init?.headers ?? {})
    },
    ...init
  });

  if (!response.ok) {
    throw new Error(`API request failed with status ${response.status}`);
  }

  const payload = (await response.json()) as ApiResponse<T>;
  return payload.data;
}

export function getQuestions(filters?: {
  type?: QuestionType;
  difficulty?: DifficultyLevel;
}): Promise<Question[]> {
  const searchParams = new URLSearchParams();

  if (filters?.type) {
    searchParams.set("type", filters.type);
  }
  if (filters?.difficulty) {
    searchParams.set("difficulty", filters.difficulty);
  }

  const queryString = searchParams.toString();
  return request<Question[]>(`/questions${queryString ? `?${queryString}` : ""}`);
}

export async function getQuestionById(id: number): Promise<Question> {
  const questions = await getQuestions();
  const question = questions.find((item) => item.id === id);

  if (!question) {
    throw new Error(`Question ${id} was not found`);
  }

  return question;
}

export function submitAttempt(payload: {
  userId: number;
  questionId: number;
  submittedAnswer: string;
}): Promise<AttemptResponse> {
  return request<AttemptResponse>("/attempts", {
    method: "POST",
    body: JSON.stringify(payload)
  });
}
