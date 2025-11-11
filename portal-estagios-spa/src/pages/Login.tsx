import { useState } from "react";
import { api } from "../api/client";
import { useAuth } from "../auth/AuthContext";
import jwtDecode from "jwt-decode";
import { useNavigate } from "react-router-dom";

export default function Login() {
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const nav = useNavigate();
  const { setAuth } = useAuth();

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    const { data } = await api.post("/auth/login", { email, senha });
    localStorage.setItem("token", data.token);
    const d: any = jwtDecode(data.token);
    const roles = d?.roles || d?.claims?.roles || [];
    setAuth({ token: data.token, roles });
    nav("/");
  }

  return (
    <form onSubmit={onSubmit}>
      <h1>Login</h1>
      <input placeholder="Email" value={email} onChange={e => setEmail(e.target.value)} />
      <input placeholder="Senha" type="password" value={senha} onChange={e => setSenha(e.target.value)} />
      <button>Entrar</button>
    </form>
  );
}
