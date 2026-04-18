import { Link, NavLink, Outlet } from "react-router-dom";

export function AppShell() {
  return (
    <div className="app-shell">
      <header className="topbar">
        <Link className="brand" to="/questions">
          AlgoMentor AI
        </Link>
        <nav className="nav-links">
          <NavLink to="/questions">Questions</NavLink>
        </nav>
      </header>

      <main className="page-container">
        <Outlet />
      </main>
    </div>
  );
}
