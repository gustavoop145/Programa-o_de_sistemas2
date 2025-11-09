// src/main/java/com/portalestagios/dto/vaga/VagaResponseDTO.java
package com.portalestagios.dto.vaga;

import com.portalestagios.entity.enums.Modalidade;
import com.portalestagios.entity.enums.StatusVaga;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "VagaResponseDTO")
public record VagaResponseDTO(
        Long id,
        String titulo,
        String descricao,
        String area,
        String localizacao,
        Modalidade modalidade,
        Integer cargaHoraria,
        String requisitos,
        StatusVaga status,
        Long empresaId,
        String empresaNome
) {}