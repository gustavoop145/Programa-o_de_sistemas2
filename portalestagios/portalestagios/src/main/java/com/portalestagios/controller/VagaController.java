package com.portalestagios.controller;

import com.portalestagios.dto.vaga.VagaCreateDTO;
import com.portalestagios.dto.vaga.VagaResponseDTO;
import com.portalestagios.dto.vaga.VagaUpdateDTO;
import com.portalestagios.entity.Empresa;
import com.portalestagios.service.VagaService;
import com.portalestagios.repository.EmpresaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Vagas")
@RestController
@RequestMapping("/api/vagas")
public class VagaController {

    private final VagaService vagaService;
    private final EmpresaRepository empresaRepository;

    public VagaController(VagaService vagaService, EmpresaRepository empresaRepository) {
        this.vagaService = vagaService;
        this.empresaRepository = empresaRepository;
    }

    @Operation(summary = "Listar vagas (pública, com filtros)")
    @GetMapping
    public Page<VagaResponseDTO> list(Pageable pageable,
                                      @RequestParam(required = false) Long areaId,
                                      @RequestParam(required = false) String modalidade,
                                      @RequestParam(required = false) String localizacao,
                                      @RequestParam(required = false, defaultValue = "false") Boolean abertas) {
        return vagaService.list(pageable, areaId, modalidade, localizacao, abertas);
    }

    @Operation(summary = "Detalhar vaga (público)")
    @GetMapping("/{id}")
    public VagaResponseDTO getById(@PathVariable Long id) {
        return vagaService.getById(id);
    }

    @Operation(summary = "Vagas recomendadas por áreas do estudante (público por enquanto)")
    @GetMapping("/recomendadas")
    public List<VagaResponseDTO> recomendadas(@RequestParam Long estudanteId,
                                              @RequestParam(defaultValue = "10") int limit) {
        return vagaService.recomendadas(estudanteId, limit);
    }

    @Operation(summary = "Criar vaga (empresa)")
    @PreAuthorize("hasRole('EMPRESA')")
    @PostMapping
    public ResponseEntity<VagaResponseDTO> create(@Valid @RequestBody VagaCreateDTO dto,
                                                  Authentication auth) {
        // pega empresa pelo email do usuário logado
        String email = auth != null ? auth.getName() : null;
        Empresa empresa = empresaRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("Empresa do token não encontrada: " + email));

        return ResponseEntity.ok(vagaService.create(dto, empresa));
    }

    @Operation(summary = "Atualizar vaga (empresa dona)")
    @PreAuthorize("hasRole('EMPRESA')")
    @PutMapping("/{id}")
    public ResponseEntity<VagaResponseDTO> update(@PathVariable Long id,
                                                  @Valid @RequestBody VagaUpdateDTO dto,
                                                  Authentication auth) {
        String email = auth != null ? auth.getName() : null;
        Empresa empresa = empresaRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("Empresa do token não encontrada: " + email));

        return ResponseEntity.ok(vagaService.update(id, dto, empresa.getId()));
    }

    @Operation(summary = "Remover vaga (empresa dona)")
    @PreAuthorize("hasRole('EMPRESA')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        String email = auth != null ? auth.getName() : null;
        Empresa empresa = empresaRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("Empresa do token não encontrada: " + email));

        vagaService.delete(id, empresa.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Encerrar vaga (empresa dona)")
    @PreAuthorize("hasRole('EMPRESA')")
    @PatchMapping("/{id}/encerrar")
    public ResponseEntity<VagaResponseDTO> encerrar(@PathVariable Long id, Authentication auth) {
        String email = auth != null ? auth.getName() : null;
        Empresa empresa = empresaRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("Empresa do token não encontrada: " + email));

        return ResponseEntity.ok(vagaService.encerrar(id, empresa.getId()));
    }
}