import { BrowserRouter, Routes, Route } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { AuthProvider } from "./auth/AuthContext";
import Protected from "./auth/Protected";
import Login from "./pages/Login";
import EstudanteHome from "./pages/EstudanteHome";
import EmpresaHome from "./pages/EmpresaHome";
import AdminDashboard from "./pages/AdminDashboard";

const qc = new QueryClient();

export default function App() {
  return (
    <QueryClientProvider client={qc}>
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/" element={<Protected><EstudanteHome /></Protected>} />
            <Route path="/empresa" element={<Protected role="ROLE_EMPRESA"><EmpresaHome /></Protected>} />
            <Route path="/admin" element={<Protected role="ROLE_ADMIN"><AdminDashboard /></Protected>} />
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </QueryClientProvider>
  );
}
