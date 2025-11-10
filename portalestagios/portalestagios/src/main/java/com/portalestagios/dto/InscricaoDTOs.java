package com.portalestagios.dto;

import com.portalestagios.entity.enums.StatusInscricao;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

public class InscricaoDTOs {

  public static record NovaInscricaoRequest(
      @NotNull Long estudanteId,
      @NotNull Long vagaId
  ) implements Serializable {}

  public static record InscricaoResponse(
      Long id,
      Long estudanteId,
      Long vagaId,
      LocalDateTime dataInscricao,
      StatusInscricao status
  ) implements Serializable {}
}