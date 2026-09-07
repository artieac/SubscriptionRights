import type { ReactNode } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { RoleGuard } from "./RoleGuard";

export function Layout({ children }: { children: ReactNode }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  async function handleLogout() {
    await logout();
    navigate("/login");
  }

  return (
    <div className="app-layout">
      <header className="app-header">
        <Link to="/applications" className="app-title">
          SubscriptionRights
        </Link>
        <nav className="app-nav">
          <Link to="/applications">Applications</Link>
          <RoleGuard requireAdmin>
            <Link to="/users">Users</Link>
          </RoleGuard>
        </nav>
        {user && (
          <div className="app-user">
            <span>
              {user.displayName} {user.isAdmin && <em>(admin)</em>}
            </span>
            <button type="button" onClick={handleLogout}>
              Log out
            </button>
          </div>
        )}
      </header>
      <main className="app-content">{children}</main>
    </div>
  );
}
