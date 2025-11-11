import { ReactElement } from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "./AuthContext";

type Props = {
  children: ReactElement;
  role?: string; // ex: "ROLE_ADMIN"
};

export default function PrivateRoute({ children, role }: Props) {
  const { token, roles } = useAuth();

  // se não tiver token → redireciona para login
  if (!token) {
    return <Navigate to="/login" replace />;
  }

  // se exigir role específica e o usuário não tiver → redireciona para home
  if (role && !roles.includes(role)) {
    return <Navigate to="/" replace />;
  }

  // se estiver tudo certo → renderiza a rota normalmente
  return children;
}