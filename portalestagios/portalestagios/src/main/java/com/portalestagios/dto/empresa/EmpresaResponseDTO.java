// src/main/java/com/portalestagios/dto/empresa/EmpresaResponseDTO.java
package com.portalestagios.dto.empresa;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EmpresaResponseDTO")
public record EmpresaResponseDTO(
        Long id,
        String nome,
        String cnpj,
        String email,
        String telefone,
        String endereco,
        String areasAtuacao
) {}