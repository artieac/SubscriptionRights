import { Navigate, Route, Routes } from "react-router-dom";
import { LoginLandingPage } from "../pages/LoginLandingPage";
import { UnauthorizedPage } from "../pages/UnauthorizedPage";
import { ApplicationsPage } from "../pages/ApplicationsPage";
import { UsersPage } from "../pages/UsersPage";
import { ApplicationDetailPage } from "../pages/ApplicationDetailPage";
import { SubscriptionPlansTab } from "../pages/SubscriptionPlansTab";
import { SubscriptionEntitlementsTab } from "../pages/SubscriptionEntitlementsTab";
import { SubscriptionPlanGrantsTab } from "../pages/SubscriptionPlanGrantsTab";
import { SubscriptionPlanSetsTab } from "../pages/SubscriptionPlanSetsTab";
import { ApiTokensTab } from "../pages/ApiTokensTab";
import { ProtectedRoute } from "./ProtectedRoute";

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/applications" replace />} />
      <Route path="/login" element={<LoginLandingPage />} />
      <Route path="/unauthorized" element={<UnauthorizedPage />} />

      <Route
        path="/applications"
        element={
          <ProtectedRoute>
            <ApplicationsPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/users"
        element={
          <ProtectedRoute>
            <UsersPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/applications/:appId"
        element={
          <ProtectedRoute>
            <ApplicationDetailPage />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="plans" replace />} />
        <Route path="plans" element={<SubscriptionPlansTab />} />
        <Route path="entitlements" element={<SubscriptionEntitlementsTab />} />
        <Route path="grants" element={<SubscriptionPlanGrantsTab />} />
        <Route path="plan-sets" element={<SubscriptionPlanSetsTab />} />
        <Route path="api-tokens" element={<ApiTokensTab />} />
      </Route>

      <Route path="*" element={<Navigate to="/applications" replace />} />
    </Routes>
  );
}
