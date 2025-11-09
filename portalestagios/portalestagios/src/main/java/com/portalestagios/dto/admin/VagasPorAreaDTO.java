// src/main/java/com/portalestagios/dto/admin/VagasPorAreaDTO.java
package com.portalestagios.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "VagasPorAreaDTO")
public record VagasPorAreaDTO(
        @Schema(example = "Tecnologia") String area,
        @Schema(example = "10") long total
) {}