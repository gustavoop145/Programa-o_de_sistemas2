import { useEffect, useMemo } from "react";
import { useForm } from "react-hook-form";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { listarAreas } from "../api/areas";
import { criarVaga, atualizarVaga, obterVaga, type Vaga, type VagaUpdate } from "../api/vagas";
import { useNavigate, useParams } from "react-router-dom";

type FormData = {
  titulo: string;
  descricao: string;
  areaId: number | string;
  localizacao: string;
  modalidade: string;
  cargaHoraria: number | string;
  requisitos?: string;
  empresaId: number | string;
};

export default function VagaForm() {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const nav = useNavigate();
  const qc = useQueryClient();

  const { data: areas } = useQuery({ queryKey: ["areas"], queryFn: listarAreas });

  const empresaIdDefault = useMemo(() => localStorage.getItem("empresaId") || "1", []);

  const { data: vaga } = useQuery({
    queryKey: ["vaga", id],
    queryFn: () => obterVaga(Number(id)),
    enabled: isEdit,
  });

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FormData>({
    defaultValues: {
      titulo: "",
      descricao: "",
      areaId: "",
      localizacao: "",
      modalidade: "",
      cargaHoraria: "",
      requisitos: "",
      empresaId: empresaIdDefault,
    },
  });

  useEffect(() => {
    if (!vaga || !isEdit) return;

    const v = vaga as Vaga;

    const areaId =
      typeof v.area === "object"
        ? (v.area as any).id
        : (v as any).areaId ?? "";

    const empId =
      typeof v.empresa === "object"
        ? (v.empresa as any)?.id
        : (v as any).empresaId ?? empresaIdDefault;

    reset({
      titulo: v.titulo,
      descricao: v.descricao,
      areaId,
      localizacao: v.localizacao,
      modalidade: v.modalidade,
      cargaHoraria: (v as any).cargaHoraria ?? "",
      requisitos: v.requisitos ?? "",
      empresaId: empId,
    });
  }, [vaga, isEdit, reset, empresaIdDefault]);

  const mSalvar = useMutation({
    mutationFn: (form: FormData) => {
      const payloadBase = {
        titulo: form.titulo.trim(),
        descricao: form.descricao.trim(),
        areaId: Number(form.areaId),
        localizacao: form.localizacao.trim(),
        modalidade: form.modalidade.trim(),
        cargaHoraria: Number(form.cargaHoraria),
        requisitos: form.requisitos?.trim() || undefined,
        empresaId: Number(form.empresaId),
      };

      if (isNaN(payloadBase.areaId) || isNaN(payloadBase.cargaHoraria) || isNaN(payloadBase.empresaId)) {
        throw new Error("Preencha Área, Carga Horária e Empresa corretamente.");
      }

      if (isEdit) {
        const upd: VagaUpdate = {
          titulo: payloadBase.titulo,
          descricao: payloadBase.descricao,
          localizacao: payloadBase.localizacao,
          modalidade: payloadBase.modalidade,
          cargaHoraria: payloadBase.cargaHoraria,
          requisitos: payloadBase.requisitos,
        };
        return atualizarVaga(Number(id), upd);
      }

      return criarVaga(payloadBase);
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["vagas"] });
      nav("/vagas");
    },
  });

  return (
    <div className="container" style={{ maxWidth: 720, margin: "0 auto" }}>
      <h1>{isEdit ? "Editar Vaga" : "Nova Vaga"}</h1>

      <form onSubmit={handleSubmit((f) => mSalvar.mutate(f))} style={{ display: "grid", gap: 10 }}>
        <label>
          Título
          <input {...register("titulo", { required: "Informe o título" })} />
          {errors.titulo && <small style={{ color: "crimson" }}>{errors.titulo.message}</small>}
        </label>

        <label>
          Descrição
          <textarea rows={4} {...register("descricao", { required: "Informe a descrição" })} />
          {errors.descricao && <small style={{ color: "crimson" }}>{errors.descricao.message}</small>}
        </label>

        <label>
          Área
          <select {...register("areaId", { required: "Selecione uma área" })}>
            <option value="">Selecione</option>
            {areas?.map((a) => (
              <option key={a.id} value={a.id}>
                {a.nome}
              </option>
            ))}
          </select>
          {errors.areaId && <small style={{ color: "crimson" }}>{errors.areaId.message}</small>}
        </label>

        <label>
          Modalidade
          <input
            placeholder="Presencial / Remoto / Híbrido"
            {...register("modalidade", { required: "Informe a modalidade" })}
          />
          {errors.modalidade && <small style={{ color: "crimson" }}>{errors.modalidade.message}</small>}
        </label>

        <label>
          Localização
          <input
            placeholder="Cidade/UF"
            {...register("localizacao", { required: "Informe a localização" })}
          />
          {errors.localizacao && <small style={{ color: "crimson" }}>{errors.localizacao.message}</small>}
        </label>

        <label>
          Carga horária (h/semana)
          <input type="number" min={1} {...register("cargaHoraria", { required: "Informe a carga horária" })} />
          {errors.cargaHoraria && <small style={{ color: "crimson" }}>{errors.cargaHoraria.message}</small>}
        </label>

        <label>
          Requisitos (opcional)
          <textarea rows={3} {...register("requisitos")} />
        </label>

        <label>
          Empresa ID (temporário)
          <input type="number" {...register("empresaId", { required: "Informe o ID da empresa" })} />
          {errors.empresaId && <small style={{ color: "crimson" }}>{errors.empresaId.message}</small>}
        </label>

        <div style={{ display: "flex", gap: 8 }}>
          <button type="submit" disabled={mSalvar.isPending}>
            {mSalvar.isPending ? "Salvando..." : "Salvar"}
          </button>
          <button type="button" onClick={() => nav("/vagas")}>Cancelar</button>
        </div>
      </form>
    </div>
  );
}
