import { api } from "../api/client";

export async function login(email: string, senha: string) {
  const { data } = await api.post("/auth/login", { email, senha });
  // backend retorna { accessToken, tokenType, expiresIn? }
  localStorage.setItem("accessToken", data.accessToken);
  return data;
}

export function logout() {
  localStorage.removeItem("accessToken");
}