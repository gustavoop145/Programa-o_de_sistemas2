package com.portalestagios.dto;

import com.portalestagios.entity.enums.Modalidade;
import com.portalestagios.entity.enums.StatusVaga;
import jakarta.validation.constraints.*;
import java.io.Serializable;

public class VagaDTOs {

  public static record VagaRequest(
      @NotBlank String titulo,
      @NotBlank String descricao,
      @NotNull Long areaId,
      @NotBlank String localizacao,
      @NotNull Modalidade modalidade,
      @Min(1) @Max(44) int cargaHoraria,
      @NotBlank String requisitos,
      @NotNull Long empresaId
  ) implements Serializable {}

  public static record VagaResponse(
      Long id,
      String titulo,
      String descricao,
      String area,
      String localizacao,
      Modalidade modalidade,
      int cargaHoraria,
      String requisitos,
      StatusVaga status,
      Long empresaId
  ) implements Serializable {}
}