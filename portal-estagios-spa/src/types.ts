// Tipos reutilizáveis
export type Page<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
};

export type Area = {
  id: number;
  nome: string;
};

export type Vaga = {
  id: number;
  titulo: string;
  descricao: string;
  area: Area | { id: number; nome?: string } | string;
  localizacao: string;
  modalidade: string;
  cargaHoraria?: number;
  requisitos?: string | null;
  status?: "ABERTA" | "ENCERRADA";
  empresa?: { id: number; nome?: string; email?: string } | { id: number } | number;
};
