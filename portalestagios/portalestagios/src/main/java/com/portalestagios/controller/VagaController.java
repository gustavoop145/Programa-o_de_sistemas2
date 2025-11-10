// src/main/java/com/portalestagios/controller/VagaController.java
package com.portalestagios.controller;

import com.portalestagios.entity.*;
import com.portalestagios.entity.enums.StatusVaga;
import com.portalestagios.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

    // ---- util: checar se a empresa dona da vaga é o usuário logado (auth.getName() = email)
    private boolean isOwner(Vaga vaga, Authentication auth) {
        if (vaga == null || auth == null || vaga.getEmpresa() == null) return false;
        String emailLogado = auth.getName();
        String emailEmpresa = vaga.getEmpresa().getEmail();
        return emailEmpresa != null && emailEmpresa.equalsIgnoreCase(emailLogado);
    }

    // ---- criar vaga (apenas EMPRESA). Faz owner-check: o email logado deve ser da empresa informada
    @PreAuthorize("hasRole('EMPRESA')")
    @PostMapping
    public ResponseEntity<Vaga> create(@RequestParam Long empresaId,
                                       @RequestParam Long areaId,
                                       @Valid @RequestBody Vaga dto,
                                       Authentication auth) {
        Empresa empresa = empresaRepository.findById(empresaId).orElse(null);
        AreaInteresse area = areaInteresseRepository.findById(areaId).orElse(null);
        if (empresa == null || area == null) return ResponseEntity.badRequest().build();

        // owner-check: só a própria empresa logada pode criar vaga para ela
        if (auth == null || !empresa.getEmail().equalsIgnoreCase(auth.getName())) {
            return ResponseEntity.status(403).build();
        }

        dto.setEmpresa(empresa);
        dto.setArea(area);
        dto.setStatus(StatusVaga.ABERTA);
        Vaga saved = vagaRepository.save(dto);
        return ResponseEntity.created(URI.create("/api/vagas/" + saved.getId())).body(saved);
    }

    // ---- listar (mantém seu contrato atual com filtro por status string)
    @GetMapping
    public List<Vaga> list(@RequestParam(required = false) String status) {
        if (status == null) return vagaRepository.findAll();
        try {
            return vagaRepository.findByStatus(StatusVaga.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return vagaRepository.findAll();
        }
    }

    // ---- buscar por id (publico)
    @GetMapping("/{id}")
    public ResponseEntity<Vaga> findById(@PathVariable Long id) {
        return vagaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ---- editar (apenas EMPRESA dona da vaga)
    @PreAuthorize("hasRole('EMPRESA')")
    @PutMapping("/{id}")
    public ResponseEntity<Vaga> update(@PathVariable Long id,
                                       @Valid @RequestBody Vaga dto,
                                       Authentication auth) {
        return vagaRepository.findById(id).map(v -> {
            if (!isOwner(v, auth)) return ResponseEntity.status(403).build();

            v.setTitulo(dto.getTitulo());
            v.setDescricao(dto.getDescricao());
            v.setLocalizacao(dto.getLocalizacao());
            v.setModalidade(dto.getModalidade());
            v.setCargaHoraria(dto.getCargaHoraria());
            v.setRequisitos(dto.getRequisitos());
            // area e empresa permanecem; status só muda via /encerrar
            return ResponseEntity.ok(vagaRepository.save(v));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ---- encerrar vaga (apenas EMPRESA dona da vaga)
    @PreAuthorize("hasRole('EMPRESA')")
    @PostMapping("/{id}/encerrar")
    public ResponseEntity<Void> encerrar(@PathVariable Long id, Authentication auth) {
        return vagaRepository.findById(id)
                .map(v -> {
                    if (!isOwner(v, auth)) return ResponseEntity.status(403).build();
                    v.setStatus(StatusVaga.ENCERRADA);
                    vagaRepository.save(v);
                    return ResponseEntity.noContent().<Void>build(); // força Void
                })
                .orElseGet(() -> ResponseEntity.notFound().build()); // evita ResponseEntity<Object>
    }

    // ---- RECOMENDADAS para o estudante logado (primeira versão: recebe estudanteId por query)
    // retorna vagas ABERTAS das áreas que o estudante marcou como interesse
    @PreAuthorize("hasRole('ESTUDANTE')")
    @GetMapping("/recomendadas")
    public ResponseEntity<List<Vaga>> recomendadas(@RequestParam Long estudanteId) {
        // pega as áreas vinculadas ao estudante
        var areas = areaInteresseRepository.findAllByEstudantes_Id(estudanteId);
        if (areas.isEmpty()) return ResponseEntity.ok(List.of());

        var areaIds = areas.stream().map(AreaInteresse::getId).toList();
        // se você já adicionou no VagaRepository o método com IN e status com paginação, pode usar.
        // aqui, para manter compatibilidade, buscamos todas e filtramos em memória:
        var todas = vagaRepository.findAll();
        var filtradas = todas.stream()
                .filter(v -> v.getStatus() == StatusVaga.ABERTA)
                .filter(v -> v.getArea() != null && areaIds.contains(v.getArea().getId()))
                .toList();

        return ResponseEntity.ok(filtradas);
    }
}