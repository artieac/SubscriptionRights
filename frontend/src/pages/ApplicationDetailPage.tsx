import { useEffect, useState } from "react";
import { NavLink, Outlet, useParams } from "react-router-dom";
import { ApplicationRepository } from "../api/ApplicationRepository";
import type { ApplicationDto } from "../models/ApplicationDto";
import { LoadingSpinner } from "../components/LoadingSpinner";

export function ApplicationDetailPage() {
  const { appId } = useParams<{ appId: string }>();
  const applicationId = Number(appId);
  const [application, setApplication] = useState<ApplicationDto | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    ApplicationRepository.getById(applicationId)
      .then((result) => {
        if (!cancelled) setApplication(result);
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [applicationId]);

  if (loading) {
    return <LoadingSpinner />;
  }

  if (!application) {
    return <p>Application not found.</p>;
  }

  return (
    <div className="application-detail-page">
      <div className="page-header">
        <h1>{application.name}</h1>
      </div>
      <p className="app-external-id">
        External ID: <code>{application.externalId}</code>
      </p>
      {application.description && <p className="app-description">{application.description}</p>}

      <nav className="tab-bar">
        <NavLink to="plans" className={({ isActive }) => (isActive ? "tab active" : "tab")}>
          Subscription Plans
        </NavLink>
        <NavLink to="entitlements" className={({ isActive }) => (isActive ? "tab active" : "tab")}>
          Subscription Entitlements
        </NavLink>
        <NavLink to="grants" className={({ isActive }) => (isActive ? "tab active" : "tab")}>
          Subscription Plan Grants
        </NavLink>
        <NavLink to="plan-sets" className={({ isActive }) => (isActive ? "tab active" : "tab")}>
          Subscription Plan Sets
        </NavLink>
        <NavLink to="api-tokens" className={({ isActive }) => (isActive ? "tab active" : "tab")}>
          API Tokens
        </NavLink>
      </nav>

      <div className="tab-content">
        <Outlet context={{ applicationId, externalId: application.externalId }} />
      </div>
    </div>
  );
}
