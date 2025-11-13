package com.portalestagios.util;

import com.portalestagios.dto.inscricao.InscricaoResponseDTO;
import com.portalestagios.dto.vaga.VagaResponseDTO;
import com.portalestagios.entity.Inscricao;
import com.portalestagios.entity.Vaga;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class MapperUtils {

    private MapperUtils() {}

    // Converte lista usando um mapper fornecido
    public static <S, D> List<D> convertList(List<S> source, Function<S, D> mapper) {
        if (source == null) return List.of();
        return source.stream().map(mapper).collect(Collectors.toList());
    }

    // Mapper específico: Inscricao -> InscricaoResponseDTO
    public static InscricaoResponseDTO toInscricaoResponseDTO(Inscricao i) {
        if (i == null) return null;

        Long id = i.getId();
        Long vagaId = i.getVaga() != null ? i.getVaga().getId() : null;
        Long estudanteId = i.getEstudante() != null ? i.getEstudante().getId() : null;

        String status = null;
        if (i.getStatus() != null) {
            status = i.getStatus().toString();
        }

        // Observações: se sua entidade Inscricao tiver esse campo/getter, ajuste aqui
        String observacoes = null;
        // data de inscrição (se existir)
        java.time.LocalDateTime data = null;
        try {
            data = i.getDataInscricao();
        } catch (NoSuchMethodError | AbstractMethodError ex) {
            data = null;
        }

        return new InscricaoResponseDTO(id, vagaId, estudanteId, status, observacoes, data);
    }

    // Mapper específico: Vaga -> VagaResponseDTO
    public static VagaResponseDTO toVagaResponseDTO(Vaga v) {
        if (v == null) return null;

        Long id = v.getId();
        String titulo = v.getTitulo();
        String descricao = v.getDescricao();

        // area: se for entidade (AreaInteresse) pegamos nome (ajuste se o campo for diferente)
        String area = null;
        try {
            if (v.getArea() != null) {
                // tenta pegar nome/descrição da área
                try {
                    // usualmente a entidade Area tem getNome()
                    area = (String) v.getArea().getClass().getMethod("getNome").invoke(v.getArea());
                } catch (Exception e) {
                    // fallback: tenta toString()
                    area = v.getArea().toString();
                }
            }
        } catch (NoSuchMethodError | AbstractMethodError ex) {
            area = null;
        }

        String localizacao = v.getLocalizacao();

        // modalidade: o DTO espera Modalidade (enum). Se a entidade tiver esse enum, usa diretamente.
        com.portalestagios.entity.enums.Modalidade modalidade = null;
        try {
            modalidade = v.getModalidade();
        } catch (NoSuchMethodError | AbstractMethodError ex) {
            modalidade = null;
        }

        Integer cargaHoraria = v.getCargaHoraria();
        String requisitos = v.getRequisitos();

        com.portalestagios.entity.enums.StatusVaga status = null;
        try {
            status = v.getStatus();
        } catch (NoSuchMethodError | AbstractMethodError ex) {
            status = null;
        }

        Long empresaId = null;
        String empresaNome = null;
        if (v.getEmpresa() != null) {
            try {
                empresaId = v.getEmpresa().getId();
            } catch (NoSuchMethodError | AbstractMethodError ex) {
                empresaId = null;
            }
            try {
                empresaNome = v.getEmpresa().getNome();
            } catch (NoSuchMethodError | AbstractMethodError ex) {
                empresaNome = null;
            }
        }

        return new VagaResponseDTO(
                id,
                titulo,
                descricao,
                area,
                localizacao,
                modalidade,
                cargaHoraria,
                requisitos,
                status,
                empresaId,
                empresaNome
        );
    }

    // alias para compatibilidade com chamadas que usam toDTO
    public static VagaResponseDTO toDTO(Vaga v) {
        return toVagaResponseDTO(v);
    }

    // Exemplo genérico usando BeanUtils quando quiser copiar propriedades simples
    public static <S, D> D mapBean(S source, Class<D> destClass) {
        if (source == null) return null;
        try {
            D dest = destClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, dest);
            return dest;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao mapear bean", e);
        }
    }
}