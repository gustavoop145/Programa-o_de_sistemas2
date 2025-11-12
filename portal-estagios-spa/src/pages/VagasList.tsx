import { useMemo, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { encerrarVaga, listarVagas, deletarVaga } from "../api/vagas";
import { listarAreas } from "../api/areas";
import { Link } from "react-router-dom";

type Area = { id: number; nome: string };

type VagaRow = {
  id: number;
  titulo: string;
  descricao?: string;
  area?: Area | number | null;
  localizacao?: string;
  modalidade?: string;
  status?: "ABERTA" | "ENCERRADA";
  cargaHoraria?: number | null;
  empresa?: { id: number; nome?: string; email?: string } | number | null;
};

type PageOf<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
};

export default function VagasList() {
  const qc = useQueryClient();

  const [filtros, setFiltros] = useState({
    areaId: "",
    modalidade: "",
    localizacao: "",
    abertas: "true",
    page: 0,
    size: 10,
  });

  const { data: areas } = useQuery<Area[]>({
    queryKey: ["areas"],
    queryFn: listarAreas,
  });

  const { data, isLoading } = useQuery<PageOf<VagaRow>>({
    queryKey: ["vagas", filtros],
    queryFn: async () =>
      await listarVagas({
        page: filtros.page,
        size: filtros.size,
        areaId: filtros.areaId ? Number(filtros.areaId) : undefined,
        modalidade: filtros.modalidade || undefined,
        localizacao: filtros.localizacao || undefined,
        abertas: filtros.abertas === "true",
      }),
    placeholderData: (prev) => prev,
  });

  const empresaId = useMemo(() => {
    const s = localStorage.getItem("empresaId");
    return s ? Number(s) : 1;
  }, []);

  const mEncerrar = useMutation({
    mutationFn: (id: number) => encerrarVaga(id, empresaId),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["vagas"] }),
  });

  const mDeletar = useMutation({
    mutationFn: (id: number) => deletarVaga(id, empresaId),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["vagas"] }),
  });

  return (
    <div className="container" style={{ maxWidth: 900, margin: "0 auto" }}>
      <h1>Vagas</h1>

      <div style={{ display: "grid", gap: 8, gridTemplateColumns: "1fr 1fr 1fr 1fr auto" }}>
        <select
          value={filtros.areaId}
          onChange={(e) => setFiltros((f) => ({ ...f, areaId: e.target.value }))}
        >
          <option value="">Todas as áreas</option>
          {areas?.map((a) => (
            <option key={a.id} value={a.id}>
              {a.nome}
            </option>
          ))}
        </select>

        <input
          placeholder="Modalidade (Presencial/Remoto/Híbrido)"
          value={filtros.modalidade}
          onChange={(e) => setFiltros((f) => ({ ...f, modalidade: e.target.value }))}
        />

        <input
          placeholder="Localização"
          value={filtros.localizacao}
          onChange={(e) => setFiltros((f) => ({ ...f, localizacao: e.target.value }))}
        />

        <select
          value={filtros.abertas}
          onChange={(e) => setFiltros((f) => ({ ...f, abertas: e.target.value }))}
        >
          <option value="true">Abertas</option>
          <option value="false">Todas</option>
        </select>

        <Link to="/vagas/nova" className="btn">
          + Nova Vaga
        </Link>
      </div>

      <hr />

      {isLoading ? (
        <p>Carregando…</p>
      ) : (
        <table width="100%" cellPadding={8}>
          <thead>
            <tr>
              <th>Título</th>
              <th>Área</th>
              <th>Modalidade</th>
              <th>Local</th>
              <th>Status</th>
              <th style={{ width: 220 }}>Ações</th>
            </tr>
          </thead>
          <tbody>
            {data?.content?.map((v) => {
              const areaNome =
                v?.area && typeof v.area === "object" ? (v.area as Area).nome : String(v?.area ?? "");
              return (
                <tr key={v.id}>
                  <td>{v.titulo}</td>
                  <td>{areaNome}</td>
                  <td>{v.modalidade ?? ""}</td>
                  <td>{v.localizacao ?? ""}</td>
                  <td>{v.status ?? ""}</td>
                  <td>
                    <Link to={`/vagas/${v.id}/editar`}>Editar</Link>{" "}
                    <button
                      onClick={() => mEncerrar.mutate(v.id)}
                      disabled={v.status === "ENCERRADA" || mEncerrar.isPending}
                    >
                      Encerrar
                    </button>{" "}
                    <button
                      onClick={() => {
                        if (confirm("Confirma remover?")) mDeletar.mutate(v.id);
                      }}
                      disabled={mDeletar.isPending}
                    >
                      Remover
                    </button>
                  </td>
                </tr>
              );
            })}
            {!data?.content?.length && (
              <tr>
                <td colSpan={6}>Nenhuma vaga encontrada.</td>
              </tr>
            )}
          </tbody>
        </table>
      )}

      <div style={{ marginTop: 12, display: "flex", gap: 8, alignItems: "center" }}>
        <button
          onClick={() => setFiltros((f) => ({ ...f, page: Math.max(0, (data?.number ?? 0) - 1) }))}
          disabled={!!data?.first}
        >
          ◀ Anterior
        </button>

        <span>
          Página {(data?.number ?? 0) + 1} de {data?.totalPages ?? 1}
        </span>

        <button
          onClick={() =>
            setFiltros((f) => ({
              ...f,
              page: Math.min((data?.totalPages ?? 1) - 1, (data?.number ?? 0) + 1),
            }))
          }
          disabled={!!data?.last}
        >
          Próxima ▶
        </button>
      </div>
    </div>
  );
}
