export type QuestionType = "DSA" | "SYSTEM_DESIGN";
export type DifficultyLevel = "EASY" | "MEDIUM" | "HARD";

export interface Question {
  id: number;
  title: string;
  type: QuestionType;
  difficulty: DifficultyLevel;
  expectedConcepts: string[];
  createdAt: string;
}

export interface AttemptResponse {
  id: number;
  userId: number;
  questionId: number;
  score: number;
  automatedFeedback: string;
  status: string;
  createdAt: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}
