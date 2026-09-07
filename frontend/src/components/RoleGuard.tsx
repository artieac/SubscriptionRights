import type { ReactNode } from "react";
import { useAuth } from "../context/AuthContext";

interface RoleGuardProps {
  requireAdmin?: boolean;
  children: ReactNode;
}

export function RoleGuard({ requireAdmin = false, children }: RoleGuardProps) {
  const { user } = useAuth();
  if (requireAdmin && !user?.isAdmin) {
    return null;
  }
  return <>{children}</>;
}
