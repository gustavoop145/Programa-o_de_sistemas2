// src/main/java/com/portalestagios/controller/VagaController.java
package com.portalestagios.controller;

import com.portalestagios.entity.*;
import com.portalestagios.entity.enums.StatusVaga;
import com.portalestagios.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/vagas")
@RequiredArgsConstructor
public class VagaController {

    private final VagaRepository vagaRepository;
    private final EmpresaRepository empresaRepository;
    private final AreaInteresseRepository areaInteresseRepository;

    @PreAuthorize("hasRole('EMPRESA')")
    @PostMapping
    public ResponseEntity<Vaga> create(@RequestParam Long empresaId,
                                       @RequestParam Long areaId,
                                       @Valid @RequestBody Vaga dto) {
        Empresa empresa = empresaRepository.findById(empresaId).orElse(null);
        AreaInteresse area = areaInteresseRepository.findById(areaId).orElse(null);
        if (empresa == null || area == null) return ResponseEntity.badRequest().build();

        dto.setEmpresa(empresa);
        dto.setArea(area);
        dto.setStatus(StatusVaga.ABERTA);
        Vaga saved = vagaRepository.save(dto);
        return ResponseEntity.created(URI.create("/api/vagas/" + saved.getId())).body(saved);
    }

    @GetMapping
    public List<Vaga> list(@RequestParam(required = false) String status) {
        if (status == null) return vagaRepository.findAll();
        try {
            return vagaRepository.findByStatus(StatusVaga.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return vagaRepository.findAll();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vaga> findById(@PathVariable Long id) {
        return vagaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('EMPRESA')")
    @PutMapping("/{id}")
    public ResponseEntity<Vaga> update(@PathVariable Long id, @Valid @RequestBody Vaga dto) {
        return vagaRepository.findById(id).map(v -> {
            v.setTitulo(dto.getTitulo());
            v.setDescricao(dto.getDescricao());
            v.setLocalizacao(dto.getLocalizacao());
            v.setModalidade(dto.getModalidade());
            v.setCargaHoraria(dto.getCargaHoraria());
            v.setRequisitos(dto.getRequisitos());
            // area e empresa permanecem
            return ResponseEntity.ok(vagaRepository.save(v));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('EMPRESA')")
    @PostMapping("/{id}/encerrar")
    public ResponseEntity<Void> encerrar(@PathVariable Long id) {
        return vagaRepository.findById(id)
                .map(v -> {
                    v.setStatus(StatusVaga.ENCERRADA);
                    vagaRepository.save(v);
                    return ResponseEntity.noContent().<Void>build(); // força Void
                })
                .orElseGet(() -> ResponseEntity.notFound().build()); // evita ResponseEntity<Object>
    }
}