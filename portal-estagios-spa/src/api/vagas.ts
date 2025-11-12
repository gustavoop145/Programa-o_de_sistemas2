// src/api/vagas.ts
import { api } from "./client";

/** Página padrão do Spring */
export type Page<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
};

export type Vaga = {
  id: number;
  titulo: string;
  descricao: string;
  area: { id: number; nome: string } | number;
  localizacao: string;
  modalidade: string;
  cargaHoraria: number;
  requisitos?: string | null;
  status?: "ABERTA" | "ENCERRADA";
  empresa?: { id: number; nome?: string; email?: string } | number;
};

export type VagaCreate = {
  titulo: string;
  descricao: string;
  areaId: number;
  localizacao: string;
  modalidade: string;
  cargaHoraria: number;
  requisitos?: string;
  empresaId: number;
};

export type VagaUpdate = {
  titulo: string;
  descricao: string;
  localizacao: string;
  modalidade: string;
  cargaHoraria: number;
  requisitos?: string;
  areaId?: number;
  empresaId?: number;
};

function empresaIdFromStorage(): number | undefined {
  const s = localStorage.getItem("empresaId");
  if (!s) return undefined;
  const n = Number(s);
  return isNaN(n) ? undefined : n;
}

export async function listarVagas(params?: {
  page?: number;
  size?: number;
  areaId?: number;
  modalidade?: string;
  localizacao?: string;
  abertas?: boolean;
}): Promise<Page<Vaga>> {
  const { data } = await api.get<Page<Vaga>>("/api/vagas", { params });
  return data;
}

export async function obterVaga(id: number): Promise<Vaga> {
  const { data } = await api.get<Vaga>(`/api/vagas/${id}`);
  return data;
}

export async function criarVaga(payload: VagaCreate): Promise<Vaga> {
  const { data } = await api.post<Vaga>("/api/vagas", payload);
  return data;
}

export async function atualizarVaga(id: number, payload: VagaUpdate): Promise<Vaga> {
  const { data } = await api.put<Vaga>(`/api/vagas/${id}`, payload);
  return data;
}

/**
 * deletarVaga e encerrarVaga aceitam empresaId opcional.
 * Se não informado, tentam pegar do localStorage (empresaId salvo por você).
 */
export async function deletarVaga(id: number, empresaId?: number): Promise<void> {
  const empresa = empresaId ?? empresaIdFromStorage();
  const params = empresa ? { params: { empresaId: empresa } } : {};
  await api.delete(`/api/vagas/${id}`, params);
}

export async function encerrarVaga(id: number, empresaId?: number): Promise<Vaga> {
  const empresa = empresaId ?? empresaIdFromStorage();
  const { data } = await api.patch<Vaga>(`/api/vagas/${id}/encerrar`, null, {
    params: empresa ? { empresaId: empresa } : {},
  });
  return data;
}