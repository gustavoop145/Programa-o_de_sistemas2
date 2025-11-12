import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { AuthProvider } from "./auth/AuthContext";
import Protected from "./auth/Protected";

// Páginas existentes
import Login from "./pages/Login";
import EstudanteHome from "./pages/EstudanteHome";
import EmpresaHome from "./pages/EmpresaHome";
import AdminDashboard from "./pages/AdminDashboard";
import MinhasInscricoes from "./pages/MinhasInscricoes";

// CRUD de Vagas (EMPRESA)
import VagasList from "./pages/VagasList";
import VagaForm from "./pages/VagaForm";

const qc = new QueryClient();

export default function App() {
  return (
    <QueryClientProvider client={qc}>
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            {/* Público */}
            <Route path="/login" element={<Login />} />

            {/* Estudante logado */}
            <Route
              path="/"
              element={
                <Protected>
                  <EstudanteHome />
                </Protected>
              }
            />

            {/* Empresa logada */}
            <Route
              path="/empresa"
              element={
                <Protected role="ROLE_EMPRESA">
                  <EmpresaHome />
                </Protected>
              }
            />

            {/* CRUD de Vagas (somente EMPRESA) */}
            <Route
              path="/vagas"
              element={
                <Protected role="ROLE_EMPRESA">
                  <VagasList />
                </Protected>
              }
            />
            <Route
              path="/vagas/nova"
              element={
                <Protected role="ROLE_EMPRESA">
                  <VagaForm />
                </Protected>
              }
            />
            <Route
              path="/vagas/:id/editar"
              element={
                <Protected role="ROLE_EMPRESA">
                  <VagaForm />
                </Protected>
              }
            />

            {/* Admin */}
            <Route
              path="/admin"
              element={
                <Protected role="ROLE_ADMIN">
                  <AdminDashboard />
                </Protected>
              }
            />

            <Route
              path="/inscricoes"
              element={
                <Protected role="ROLE_ESTUDANTE">
                  <MinhasInscricoes />
                </Protected>
              }
            />

            {/* Fallback */}
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </QueryClientProvider>
  );
}