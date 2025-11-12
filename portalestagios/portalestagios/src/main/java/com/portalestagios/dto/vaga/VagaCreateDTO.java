// src/main/java/com/portalestagios/dto/vaga/VagaCreateDTO.java
package com.portalestagios.dto.vaga;

import com.portalestagios.entity.enums.Modalidade;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(name = "VagaCreateDTO")
public record VagaCreateDTO(
        @NotBlank @Schema(example = "Estágio em Front-end") String titulo,
        @NotBlank @Schema(example = "Atuar no time web...") String descricao,
        @NotNull  @Schema(example = "1", description = "ID da área de interesse") Long areaId,
        @NotBlank @Schema(example = "São Paulo - SP") String localizacao,
        @NotNull  @Schema(example = "REMOTO") Modalidade modalidade,
        @NotNull  @Min(10) @Max(40) @Schema(example = "30") Integer cargaHoraria,
        @NotBlank @Schema(example = "React, CSS, Git") String requisitos
) {}