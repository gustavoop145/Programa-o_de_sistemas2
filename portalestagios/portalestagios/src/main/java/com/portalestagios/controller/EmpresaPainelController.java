package com.portalestagios.controller;

import com.portalestagios.entity.Inscricao;
import com.portalestagios.repository.EmpresaRepository;
import com.portalestagios.repository.InscricaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas/me")
@RequiredArgsConstructor
public class EmpresaPainelController {

  private final EmpresaRepository empresaRepository;
  private final InscricaoRepository inscricaoRepository;

  @GetMapping("/inscricoes")
  @PreAuthorize("hasRole('EMPRESA')")
  public List<Inscricao> minhasInscricoes(Authentication auth) {
    var empresa = empresaRepository.findByEmailIgnoreCase(auth.getName())
        .orElseThrow(() -> new IllegalStateException("Empresa não encontrada para o usuário"));
    return inscricaoRepository.findByEmpresaId(empresa.getId());
  }
}