import { api } from "./client";

export type Metrics = {
  qtdEmpresas: number;
  qtdEstudantes: number;
  qtdVagasAbertas: number;
  qtdVagasEncerradas: number;
  vagasPorArea: { area: string; total: number }[];
};

export async function getMetrics() {
  const res = await api.get<Metrics>("/api/admin/metrics");
  return res.data;
}