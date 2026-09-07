import { Link } from "react-router-dom";

export function UnauthorizedPage() {
  return (
    <div className="unauthorized-page">
      <h1>Not allowed</h1>
      <p>You don't have permission to perform that action.</p>
      <Link to="/applications">Back to Applications</Link>
    </div>
  );
}
