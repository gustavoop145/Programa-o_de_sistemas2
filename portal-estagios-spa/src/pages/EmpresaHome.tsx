import { useQuery } from "@tanstack/react-query";
import { api } from "../api/client";

export default function EmpresaHome() {
  const { data } = useQuery({
    queryKey: ["minhas-inscricoes"],
    queryFn: async () => (await api.get("/api/empresas/me/inscricoes")).data
  });

  return (
    <>
      <h1>Inscrições nas minhas vagas</h1>
      <ul>
        {data?.map((i: any) => (
          <li key={i.id}>
            Vaga #{i.vaga?.id} — Estudante: {i.estudante?.nome}
          </li>
        ))}
      </ul>
    </>
  );
}
