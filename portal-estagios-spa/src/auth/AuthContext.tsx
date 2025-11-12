import { createContext, useContext, useEffect, useState } from "react";
import { jwtDecode } from "jwt-decode";

type Auth = {
  token: string | null;
  roles: string[];
};

type AuthContextType = {
  token: string | null;
  roles: string[];
  setAuth: (auth: Auth) => void;
  logout: () => void;
};

const AuthCtx = createContext<AuthContextType>({
  token: null,
  roles: [],
  setAuth: () => {},
  logout: () => {},
});

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [token, setToken] = useState<string | null>(null);
  const [roles, setRoles] = useState<string[]>([]);

  // carrega token e roles do localStorage ao iniciar
  useEffect(() => {
    const storedToken = localStorage.getItem("accessToken");
    const storedRoles = JSON.parse(localStorage.getItem("roles") || "[]");

    if (storedToken) {
      try {
        const { exp }: any = jwtDecode(storedToken);
        if (exp && exp * 1000 < Date.now()) {
          // expirado
          localStorage.removeItem("accessToken");
          localStorage.removeItem("roles");
        } else {
          setToken(storedToken);
          setRoles(storedRoles);
        }
      } catch {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("roles");
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
