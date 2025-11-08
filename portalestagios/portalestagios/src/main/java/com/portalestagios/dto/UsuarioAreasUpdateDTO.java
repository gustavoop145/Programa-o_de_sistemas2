package com.portalestagios.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record UsuarioAreasUpdateDTO(
    @NotEmpty(message = "idsAreas não pode ser vazio")
    List<Long> idsAreas
) {}