// src/main/java/com/portalestagios/dto/inscricao/InscricaoResponseDTO.java
package com.portalestagios.dto.inscricao;

import com.portalestagios.entity.enums.StatusInscricao;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "InscricaoResponseDTO")
public record InscricaoResponseDTO(
        Long id,
        Long estudanteId,
        String estudanteNome,
        Long vagaId,
        String vagaTitulo,
        LocalDateTime dataInscricao,
        StatusInscricao status
) {}