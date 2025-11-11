import { Link } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";

export default function NavBar() {
  const { roles, logout } = useAuth();
  const isAdmin = roles.includes("ROLE_ADMIN");
  const isEmpresa = roles.includes("ROLE_EMPRESA");

  return (
    <nav style={{display:"flex", gap:12, padding:12, borderBottom:"1px solid #eee"}}>
      <Link to="/">Home</Link>
      {isEmpresa && <Link to="/empresa">Empresa</Link>}
      {isAdmin && <Link to="/admin">Admin</Link>}
      <button onClick={logout} style={{marginLeft:"auto"}}>Sair</button>
    </nav>
  );
}