import { api } from "./client";

export type Inscricao = {
  id: number;
  dataInscricao: string;
  status: string;
  estudante?: { id: number; nome?: string; email?: string } | number;
  vaga?: { id: number; titulo?: string; area?: { id: number; nome?: string } } | number;
};

export async function inscrever(vagaId: number): Promise<Inscricao> {
  const { data } = await api.post<Inscricao>("/api/inscricoes", { vagaId });
  return data;
}

export async function listarMinhasInscricoes(): Promise<Inscricao[]> {
  const { data } = await api.get<Inscricao[]>("/api/inscricoes/minhas");
  return data;
}

export async function cancelarInscricao(id: number): Promise<void> {
  await api.delete(`/api/inscricoes/${id}`);
}

export async function listarInscricoesPorVaga(vagaId: number) {
  const { data } = await api.get<Inscricao[]>(`/api/inscricoes/por-vaga/${vagaId}`);
  return data;
}