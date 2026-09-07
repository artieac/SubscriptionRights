import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { AuthRepository } from "../api/AuthRepository";
import { LoadingSpinner } from "../components/LoadingSpinner";

export function LoginLandingPage() {
  const { user, loading } = useAuth();

  if (loading) {
    return <LoadingSpinner />;
  }

  if (user) {
    return <Navigate to="/applications" replace />;
  }

  return (
    <div className="login-landing">
      <h1>SubscriptionRights</h1>
      <p>Sign in to manage applications, subscription tiers, rights, and grants.</p>
      <a className="login-button" href={AuthRepository.loginUrl()}>
        Log in
      </a>
    </div>
  );
}
