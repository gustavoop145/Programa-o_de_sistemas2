// src/main/java/com/portalestagios/controller/InscricaoController.java
package com.portalestagios.controller;

import com.portalestagios.entity.*;
import com.portalestagios.entity.enums.StatusInscricao;
import com.portalestagios.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/inscricoes")
@RequiredArgsConstructor
public class InscricaoController {

    private final InscricaoRepository inscricaoRepository;
    private final VagaRepository vagaRepository;
    private final EstudanteRepository estudanteRepository;

    @PreAuthorize("hasRole('ESTUDANTE')")
    @PostMapping
    public ResponseEntity<Inscricao> inscrever(@RequestParam Long estudanteId,
                                               @RequestParam Long vagaId,
                                               @Valid @RequestBody(required = false) Inscricao body) {
        Estudante est = estudanteRepository.findById(estudanteId).orElse(null);
        Vaga vaga = vagaRepository.findById(vagaId).orElse(null);
        if (est == null || vaga == null) return ResponseEntity.badRequest().build();

        if (inscricaoRepository.existsByEstudanteAndVaga(est, vaga)) {
            return ResponseEntity.unprocessableEntity().build();
        }

        Inscricao nova = Inscricao.builder()
                .estudante(est)
                .vaga(vaga)
                .dataInscricao(LocalDateTime.now())
                .status(StatusInscricao.ATIVA)
                .build();

        Inscricao saved = inscricaoRepository.save(nova);
        return ResponseEntity.created(URI.create("/api/inscricoes/" + saved.getId())).body(saved);
    }

    @PreAuthorize("hasRole('ESTUDANTE')")
    @GetMapping("/por-estudante/{estudanteId}")
    public List<Inscricao> listarPorEstudante(@PathVariable Long estudanteId) {
        return inscricaoRepository.findByEstudante_Id(estudanteId);
    }

    @PreAuthorize("hasRole('EMPRESA') or hasRole('ADMIN')")
    @GetMapping("/por-vaga/{vagaId}")
    public List<Inscricao> listarPorVaga(@PathVariable Long vagaId) {
        return inscricaoRepository.findByVaga_Id(vagaId);
    }
}
