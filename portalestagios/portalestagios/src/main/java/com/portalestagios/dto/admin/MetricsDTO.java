// src/main/java/com/portalestagios/dto/admin/MetricsDTO.java
package com.portalestagios.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Schema(name = "MetricsDTO")
public class MetricsDTO {
    @Schema(example = "12")
    private long qtdEmpresas;

    @Schema(example = "230")
    private long qtdEstudantes;

    @Schema(example = "18")
    private long qtdVagasAbertas;

    @Schema(example = "7")
    private long qtdVagasEncerradas;

    private List<VagasPorAreaDTO> vagasPorArea;
}