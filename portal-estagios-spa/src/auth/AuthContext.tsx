import { createContext, useContext, useEffect, useState } from "react";
import { jwtDecode } from "jwt-decode";

type Auth = { token: string | null; roles: string[] };
type Ctx = Auth & {
  setAuth: (a: Auth) => void;
  logout: () => void;
};
const AuthCtx = createContext<Ctx>({ token: null, roles: [], setAuth: () => {}, logout: () => {} });

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [token, setToken] = useState<string | null>(null);
  const [roles, setRoles] = useState<string[]>([]);

  // carrega do localStorage na primeira montagem
  useEffect(() => {
    const t = localStorage.getItem("accessToken");
    const r = JSON.parse(localStorage.getItem("roles") || "[]");
    if (t) {
      // se tiver expirado, ignora
      try {
        const { exp }: any = jwtDecode(t);
        if (exp && exp * 1000 < Date.now()) {
          localStorage.removeItem("accessToken");
          localStorage.removeItem("roles");
        } else {
          setToken(t);
          setRoles(r);
        }
      } catch {
        /* ignore */
      }
    }
  }, []);

  const setAuth = ({ token, roles }: Auth) => {
    setToken(token);
    setRoles(roles);
  };

  const logout = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("roles");
    setToken(null);
    setRoles([]);
    location.assign("/login");
  };

  return (
    <AuthCtx.Provider value={{ token, roles, setAuth, logout }}>
      {children}
    </AuthCtx.Provider>
  );
}

export const useAuth = () => useContext(AuthCtx);
