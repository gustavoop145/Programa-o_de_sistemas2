import { useQuery } from "@tanstack/react-query";
import { api } from "../api/client";

export default function AdminDashboard() {
  const { data } = useQuery({
    queryKey: ["metrics"],
    queryFn: async () => (await api.get("/api/admin/metrics")).data
  });

  return (
    <>
      <h1>Dashboard Admin</h1>
      <p>Empresas: {data?.qtdEmpresas}</p>
      <p>Estudantes: {data?.qtdEstudantes}</p>
      <p>Vagas abertas: {data?.qtdVagasAbertas}</p>
      <p>Vagas encerradas: {data?.qtdVagasEncerradas}</p>
      <h3>Vagas por área</h3>
      <ul>
        {data?.vagasPorArea?.map((v: any) => (
          <li key={v.area}>{v.area}: {v.total}</li>
        ))}
      </ul>
    </>
  );
}
