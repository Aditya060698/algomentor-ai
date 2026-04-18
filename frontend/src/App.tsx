import { Navigate, Route, BrowserRouter as Router, Routes } from "react-router-dom";
import { AppShell } from "./components/AppShell";
import { FeedbackPage } from "./pages/FeedbackPage";
import { PracticePage } from "./pages/PracticePage";
import { QuestionListPage } from "./pages/QuestionListPage";
import { SubmissionPage } from "./pages/SubmissionPage";

export function App() {
  return (
    <Router>
      <Routes>
        <Route element={<AppShell />} path="/">
          <Route element={<Navigate replace to="/questions" />} index />
          <Route element={<QuestionListPage />} path="questions" />
          <Route element={<PracticePage />} path="practice/:questionId" />
          <Route element={<SubmissionPage />} path="submit/:questionId" />
          <Route element={<FeedbackPage />} path="feedback" />
        </Route>
      </Routes>
    </Router>
  );
}
