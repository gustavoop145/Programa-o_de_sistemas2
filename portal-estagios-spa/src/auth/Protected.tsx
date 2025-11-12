// src/auth/Protected.tsx
import { Navigate } from "react-router-dom";
import { useAuth } from "./AuthContext";
import type { PropsWithChildren } from "react";

type Props = PropsWithChildren<{ role?: string }>;

export default function Protected({ children, role }: Props) {
  const { token, roles } = useAuth();

  if (!token) return <Navigate to="/login" replace />;
  if (role && !roles.includes(role)) return <Navigate to="/" replace />;

  return <>{children}</>;
}
