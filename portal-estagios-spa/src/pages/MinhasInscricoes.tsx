// src/pages/MinhasInscricoes.tsx
import React from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { listarMinhasInscricoes, cancelarInscricao, type Inscricao } from "../api/inscricoes";
import { useNavigate } from "react-router-dom";

export default function MinhasInscricoes(): JSX.Element {
  const qc = useQueryClient();
  const nav = useNavigate();

  const {
    data: inscricoes,
    isLoading,
    isError,
    error,
  } = useQuery<Inscricao[], Error>({
    queryKey: ["minhasInscricoes"],
    queryFn: listarMinhasInscricoes,
  });

  const mCancelar = useMutation<void, Error, number>({
    mutationFn: (id: number) => cancelarInscricao(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["minhasInscricoes"] }),
  });

  return (
    <div style={{ maxWidth: 900, margin: "2rem auto", fontFamily: "system-ui" }}>
      <h1>Minhas Inscrições</h1>

      <div style={{ marginBottom: 12, display: "flex", gap: 8 }}>
        <button onClick={() => nav("/vagas")}>Voltar para vagas</button>
        <button onClick={() => qc.invalidateQueries({ queryKey: ["minhasInscricoes"] })}>
          Atualizar
        </button>
      </div>

      {isLoading && <p>Carregando…</p>}

      {isError && (
        <div style={{ color: "crimson", marginBottom: 12 }}>
          Falha ao carregar inscrições: {error?.message ?? "erro desconhecido"}
        </div>
      )}

      {!isLoading && !isError && (
        <table width="100%" cellPadding={8} style={{ marginTop: 12, borderCollapse: "collapse" }}>
          <thead>
            <tr style={{ textAlign: "left", borderBottom: "1px solid #ddd" }}>
              <th>Vaga</th>
              <th>Data</th>
              <th>Status</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {inscricoes && inscricoes.length > 0 ? (
              inscricoes.map((i) => {
                const vagaTitulo =
                  typeof i.vaga === "object" && i.vaga !== null
                    ? (i.vaga as any).titulo ?? `#${(i.vaga as any).id ?? ""}`
                    : typeof i.vaga === "number"
                    ? `Vaga #${i.vaga}`
                    : "";

                const dataStr = i.dataInscricao
                  ? (() => {
                      const d = new Date(i.dataInscricao);
                      return isNaN(d.getTime()) ? i.dataInscricao : d.toLocaleString();
                    })()
                  : "";

                return (
                  <tr key={i.id} style={{ borderBottom: "1px solid #f0f0f0" }}>
                    <td>{vagaTitulo}</td>
                    <td>{dataStr}</td>
                    <td>{i.status}</td>
                    <td>
                      <button
                        onClick={() => {
                          if (!confirm("Deseja cancelar esta inscrição?")) return;
                          mCancelar.mutate(i.id);
                        }}
                        disabled={mCancelar.isPending}
                      >
                        {mCancelar.isPending ? "Cancelando..." : "Cancelar"}
                      </button>
                    </td>
                  </tr>
                );
              })
            ) : (
              <tr>
                <td colSpan={4}>Você ainda não se inscreveu em nenhuma vaga.</td>
              </tr>
            )}
          </tbody>
        </table>
      )}
    </div>
  );
}