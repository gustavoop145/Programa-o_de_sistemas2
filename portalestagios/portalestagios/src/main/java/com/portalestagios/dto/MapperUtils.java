// src/main/java/com/portalestagios/dto/MapperUtils.java
package com.portalestagios.dto;

import com.portalestagios.dto.empresa.EmpresaResponseDTO;
import com.portalestagios.dto.estudante.EstudanteResponseDTO;
import com.portalestagios.dto.vaga.VagaResponseDTO;
import com.portalestagios.dto.inscricao.InscricaoResponseDTO;
import com.portalestagios.entity.*;

import java.util.stream.Collectors;

public class MapperUtils {

    public static EmpresaResponseDTO toDTO(Empresa e) {
        return new EmpresaResponseDTO(
                e.getId(), e.getNome(), e.getCnpj(), e.getEmail(), e.getTelefone(), e.getEndereco(), e.getAreasAtuacao()
        );
    }

    public static EstudanteResponseDTO toDTO(Estudante s) {
        return new EstudanteResponseDTO(
                s.getId(), s.getNome(), s.getCpf(), s.getCurso(), s.getEmail(), s.getTelefone(),
                s.getAreasInteresse().stream().map(AreaInteresse::getNome).collect(Collectors.toSet())
        );
    }

    public static VagaResponseDTO toDTO(Vaga v) {
        return new VagaResponseDTO(
                v.getId(), v.getTitulo(), v.getDescricao(),
                v.getArea() != null ? v.getArea().getNome() : null,
                v.getLocalizacao(), v.getModalidade(), v.getCargaHoraria(),
                v.getRequisitos(), v.getStatus(),
                v.getEmpresa() != null ? v.getEmpresa().getId() : null,
                v.getEmpresa() != null ? v.getEmpresa().getNome() : null
        );
    }

    public static InscricaoResponseDTO toDTO(Inscricao i) {
        return new InscricaoResponseDTO(
                i.getId(),
                i.getEstudante() != null ? i.getEstudante().getId() : null,
                i.getEstudante() != null ? i.getEstudante().getNome() : null,
                i.getVaga() != null ? i.getVaga().getId() : null,
                i.getVaga() != null ? i.getVaga().getTitulo() : null,
                i.getDataInscricao(),
                i.getStatus()
        );
    }
}