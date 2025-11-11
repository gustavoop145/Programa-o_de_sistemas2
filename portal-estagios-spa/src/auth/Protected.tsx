import { Navigate } from "react-router-dom";
import { useAuth } from "./AuthContext";

export default function Protected({ children, role }: { children: JSX.Element, role?: string }) {
  const { auth } = useAuth();
  if (!auth.token) return <Navigate to="/login" replace />;
  if (role && !auth.roles.includes(role)) return <Navigate to="/" replace />;
  return children;
}
