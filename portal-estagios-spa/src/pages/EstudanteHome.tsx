import { useQuery } from "@tanstack/react-query";
import { api } from "../api/client";

export default function EstudanteHome() {
  const estudanteId = 1; // exemplo fixo por enquanto
  const { data } = useQuery({
    queryKey: ["recomendadas", estudanteId],
    queryFn: async () => (await api.get(`/api/vagas/recomendadas?estudanteId=${estudanteId}`)).data
  });

  return (
    <>
      <h1>Vagas recomendadas</h1>
      <ul>
        {data?.map((v: any) => (
          <li key={v.id}>{v.titulo} — {v.area?.nome || v.area}</li>
        ))}
      </ul>
    </>
  );
}
