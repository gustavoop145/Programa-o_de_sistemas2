// src/main/java/com/portalestagios/dto/vaga/VagaUpdateDTO.java
package com.portalestagios.dto.vaga;

import com.portalestagios.entity.enums.Modalidade;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(name = "VagaUpdateDTO")
public record VagaUpdateDTO(
        @NotBlank @Schema(example = "Estágio em Front-end") String titulo,
        @NotBlank @Schema(example = "Atuar no time web...") String descricao,
        @NotBlank @Schema(example = "São Paulo - SP") String localizacao,
        @NotNull  @Schema(example = "REMOTO") Modalidade modalidade,
        @NotNull  @Min(10) @Max(40) @Schema(example = "30") Integer cargaHoraria,
        @NotBlank @Schema(example = "React, CSS, Git") String requisitos
) {}