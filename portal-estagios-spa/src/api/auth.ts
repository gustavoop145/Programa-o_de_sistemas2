import { api } from "./client";

type LoginResponse =
  | { token: string; roles?: string[]; email?: string; nome?: string }
  | { accessToken: string; roles?: string[]; email?: string; nome?: string };

export async function login(email: string, senha: string) {
  const { data } = await api.post<LoginResponse>("/auth/login", { email, senha });

  // backend pode mandar "token" ou "accessToken"
  const accessToken = (data as any).accessToken ?? (data as any).token;
  if (!accessToken) throw new Error("Resposta de login sem token.");

  localStorage.setItem("accessToken", accessToken);

  // se já veio roles na resposta, salva; se não, vamos descobrir no /me
  if (Array.isArray((data as any).roles)) {
    localStorage.setItem("roles", JSON.stringify((data as any).roles));
  }

  // completa dados do usuário (roles, ids…)
  const me = await meProfile();
  if (me?.roles) localStorage.setItem("roles", JSON.stringify(me.roles));
  if (me?.empresaId != null) localStorage.setItem("empresaId", String(me.empresaId));
  if (me?.estudanteId != null) localStorage.setItem("estudanteId", String(me.estudanteId));

  return me;
}

export async function meProfile(): Promise<{
  authenticated: boolean;
  email?: string;
  nome?: string;
  roles?: string[];
  empresaId?: number | null;
  estudanteId?: number | null;
}> {
  const { data } = await api.get("/api/users/me");
  return data;
}

export function logout() {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("roles");
  localStorage.removeItem("empresaId");
  localStorage.removeItem("estudanteId");
}