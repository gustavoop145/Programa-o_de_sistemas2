import { api } from "./client";

export async function listarPorVaga(vagaId: number) {
  const res = await api.get(`/api/inscricoes/por-vaga/${vagaId}`);
  return res.data;
}