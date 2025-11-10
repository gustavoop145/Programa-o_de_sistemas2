// src/main/java/com/portalestagios/controller/InscricaoController.java
package com.portalestagios.controller;

import com.portalestagios.entity.*;
import com.portalestagios.entity.enums.StatusInscricao;
import com.portalestagios.entity.enums.StatusVaga;
import com.portalestagios.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

    // ========= CRIAR INSCRIÇÃO (somente ESTUDANTE logado) =========
    @PreAuthorize("hasRole('ESTUDANTE')")
    @PostMapping
    public ResponseEntity<Inscricao> inscrever(@RequestParam Long estudanteId,
                                               @RequestParam Long vagaId,
                                               Authentication auth) {
        Estudante est = estudanteRepository.findById(estudanteId).orElse(null);
        Vaga vaga = vagaRepository.findById(vagaId).orElse(null);
        if (est == null || vaga == null) return ResponseEntity.badRequest().build();

        // OWNER-CHECK: o e-mail do usuário logado (JWT subject) deve ser o e-mail do estudante
        if (auth == null || !est.getEmail().equalsIgnoreCase(auth.getName())) {
            return ResponseEntity.status(403).build();
        }

        // BLOQUEIO: não permitir inscrição em vaga ENCERRADA
        if (vaga.getStatus() == StatusVaga.ENCERRADA) {
            return ResponseEntity.status(422).build(); // Unprocessable Entity
        }

        // EVITAR DUPLICIDADE
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

    // ========= LISTAR POR ESTUDANTE (ainda aceita id por path) =========
    @PreAuthorize("hasRole('ESTUDANTE')")
    @GetMapping("/por-estudante/{estudanteId}")
    public ResponseEntity<List<Inscricao>> listarPorEstudante(@PathVariable Long estudanteId,
                                                              Authentication auth) {
        var est = estudanteRepository.findById(estudanteId).orElse(null);
        if (est == null) return ResponseEntity.notFound().build();

        // OWNER-CHECK: só o próprio estudante vê suas inscrições
        if (auth == null || !est.getEmail().equalsIgnoreCase(auth.getName())) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(inscricaoRepository.findByEstudante_Id(estudanteId));
    }

    // ========= LISTAR POR VAGA (EMPRESA dona da vaga OU ADMIN) =========
    @PreAuthorize("hasRole('EMPRESA') or hasRole('ADMIN')")
    @GetMapping("/por-vaga/{vagaId}")
    public ResponseEntity<List<Inscricao>> listarPorVaga(@PathVariable Long vagaId,
                                                         Authentication auth) {
        var vaga = vagaRepository.findById(vagaId).orElse(null);
        if (vaga == null) return ResponseEntity.notFound().build();

        // Se não for ADMIN, validar que a empresa logada é dona da vaga
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            var emailLogado = auth != null ? auth.getName() : null;
            var emailEmpresa = vaga.getEmpresa() != null ? vaga.getEmpresa().getEmail() : null;
            if (emailLogado == null || emailEmpresa == null || !emailEmpresa.equalsIgnoreCase(emailLogado)) {
                return ResponseEntity.status(403).build();
            }
        }

        return ResponseEntity.ok(inscricaoRepository.findByVaga_Id(vagaId));
    }

    // ========= (Opcional) LISTAR "MINHAS" INSCRIÇÕES PELO LOGIN =========
    // Requer no EstudanteRepository: Optional<Estudante> findByEmailIgnoreCase(String email);
    @PreAuthorize("hasRole('ESTUDANTE')")
    @GetMapping("/minhas")
    public ResponseEntity<List<Inscricao>> minhas(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        var est = estudanteRepository.findByEmailIgnoreCase(auth.getName()).orElse(null);
        if (est == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(inscricaoRepository.findByEstudante_Id(est.getId()));
    }

    // ========= (Opcional) CANCELAR INSCRIÇÃO =========
    // Apenas o estudante dono (ou ADMIN) pode cancelar. Aqui vamos deletar o registro.
    @PreAuthorize("hasRole('ESTUDANTE') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id, Authentication auth) {
        var insc = inscricaoRepository.findById(id).orElse(null);
        if (insc == null) return ResponseEntity.notFound().build();

        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            var emailLogado = auth != null ? auth.getName() : null;
            var emailEst = insc.getEstudante() != null ? insc.getEstudante().getEmail() : null;
            if (emailLogado == null || emailEst == null || !emailEst.equalsIgnoreCase(emailLogado)) {
                return ResponseEntity.status(403).build();
            }
        }

        inscricaoRepository.delete(insc);
        return ResponseEntity.noContent().build();
    }
}