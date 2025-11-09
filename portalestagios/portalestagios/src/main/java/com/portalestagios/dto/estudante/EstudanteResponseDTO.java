// src/main/java/com/portalestagios/dto/estudante/EstudanteResponseDTO.java
package com.portalestagios.dto.estudante;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

@Schema(name = "EstudanteResponseDTO")
public record EstudanteResponseDTO(
        Long id,
        String nome,
        String cpf,
        String curso,
        String email,
        String telefone,
        Set<String> areasInteresse
) {}