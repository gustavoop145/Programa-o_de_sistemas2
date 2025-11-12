import { useState } from "react";
import { api } from "../api/client";
import { useAuth } from "../auth/AuthContext";
import { jwtDecode } from "jwt-decode";
import { useNavigate } from "react-router-dom";

type JwtPayload = {
  sub?: string;
  roles?: string[];
  authorities?: string[];
  scope?: string;
  claims?: { roles?: string[] };
  exp?: number;
};

export default function Login() {
  const [email, setEmail] = useState("admin@teste.com");
  const [senha, setSenha] = useState("123456");
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState<string | null>(null);

  const nav = useNavigate();
  const { setAuth } = useAuth();

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    setErr(null);
    setLoading(true);
    try {
      const { data } = await api.post("/auth/login", { email, senha });
      const token: string = data?.accessToken || data?.token || data?.jwt || "";

      if (!token) throw new Error("Resposta sem token.");

      // salva o token
      localStorage.setItem("accessToken", token);

      // extrai roles de várias possíveis estruturas
      const decoded = jwtDecode<JwtPayload>(token);
      let roles: string[] =
        decoded?.roles ||
        decoded?.claims?.roles ||
        decoded?.authorities ||
        (decoded?.scope ? decoded.scope.split(" ") : []) ||
        [];

      // normaliza para "ROLE_XXX"
      roles = roles.map((r) => (r.startsWith("ROLE_") ? r : `ROLE_${r}`));
      localStorage.setItem("roles", JSON.stringify(roles));

      // atualiza contexto
      setAuth({ token, roles });

      // navegação automática conforme papel
      if (roles.includes("ROLE_ADMIN")) {
        nav("/admin", { replace: true });
      } else if (roles.includes("ROLE_EMPRESA")) {
        nav("/empresa", { replace: true });
      } else {
        nav("/", { replace: true });
      }
    } catch (e: any) {
      setErr(
        e?.response?.data?.message ||
          e?.message ||
          "Falha ao entrar. Verifique as credenciais."
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <form
      onSubmit={onSubmit}
      style={{ maxWidth: 420, margin: "4rem auto", fontFamily: "system-ui" }}
    >
      <h1>Login</h1>

      <label>Email</label>
      <input
        placeholder="Email"
        value={email}
        onChange={(ev) => setEmail(ev.target.value)}
        type="email"
        required
        style={{ width: "100%", padding: 10, margin: "6px 0 14px" }}
      />

      <label>Senha</label>
      <input
        placeholder="Senha"
        type="password"
        value={senha}
        onChange={(ev) => setSenha(ev.target.value)}
        required
        style={{ width: "100%", padding: 10, margin: "6px 0 14px" }}
      />

      {err && <div style={{ color: "crimson", marginBottom: 10 }}>{err}</div>}

      <button disabled={loading} style={{ padding: "10px 16px" }}>
        {loading ? "Entrando..." : "Entrar"}
      </button>
    </form>
  );
}
