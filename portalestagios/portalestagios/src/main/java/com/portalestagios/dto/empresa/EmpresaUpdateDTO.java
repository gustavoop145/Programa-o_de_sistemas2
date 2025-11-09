// src/main/java/com/portalestagios/dto/empresa/EmpresaUpdateDTO.java
package com.portalestagios.dto.empresa;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(name = "EmpresaUpdateDTO")
public record EmpresaUpdateDTO(
        @NotBlank @Schema(example = "Acme Ltda.") String nome,
        @NotBlank @Schema(example = "12.345.678/0001-99") String cnpj,
        @Email @NotBlank @Schema(example = "contato@acme.com") String email,
        @NotBlank @Schema(example = "+55 11 99999-0000") String telefone,
        @NotBlank @Schema(example = "Av. Paulista, 1000 - SP") String endereco,
        @Schema(example = "Tecnologia;Educação") String areasAtuacao
) {}