import { createContext, useContext, useState } from "react";
import jwtDecode from "jwt-decode";

type Auth = { token: string | null, roles: string[] };
const AuthCtx = createContext<{ auth: Auth, setAuth: (a: Auth) => void }>({ auth: { token: null, roles: [] }, setAuth: () => {} });
export const useAuth = () => useContext(AuthCtx);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [auth, setAuth] = useState<Auth>(() => {
    const t = localStorage.getItem("token");
    if (!t) return { token: null, roles: [] };
    try {
      const d: any = jwtDecode(t);
      const roles = d?.roles || d?.claims?.roles || [];
      return { token: t, roles };
    } catch {
      return { token: null, roles: [] };
    }
  });

  return <AuthCtx.Provider value={{ auth, setAuth }}>{children}</AuthCtx.Provider>;
}
