import { api } from "./client";

export type Area = { id: number; nome: string };

export async function listarAreas() {
  const { data } = await api.get("/api/areas");
  return data as Area[];
}