// src/main/java/com/portalestagios/dto/estudante/EstudanteCreateDTO.java
package com.portalestagios.dto.estudante;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.util.Set;

@Schema(name = "EstudanteCreateDTO")
public record EstudanteCreateDTO(
        @NotBlank @Schema(example = "Melissa Rodrigues") String nome,
        @NotBlank @Schema(example = "123.456.789-00") String cpf,
        @NotBlank @Schema(example = "Sistemas de Informação") String curso,
        @Email @NotBlank @Schema(example = "melissa@email.com") String email,
        @NotBlank @Schema(example = "+55 11 90000-0000") String telefone,
        @Schema(description = "IDs das áreas de interesse") Set<Long> areasIds
) {}