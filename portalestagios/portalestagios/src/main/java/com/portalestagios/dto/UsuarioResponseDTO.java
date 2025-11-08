package com.portalestagios.dto;

import java.util.List;

public record UsuarioResponseDTO(
    Long id,
    String nome,
    String email,
    List<AreaDTO> areas
) {
  public record AreaDTO(Long id, String nome) {}
}