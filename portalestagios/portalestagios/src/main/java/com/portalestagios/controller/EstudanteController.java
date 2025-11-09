// src/main/java/com/portalestagios/controller/EstudanteController.java
package com.portalestagios.controller;

import com.portalestagios.entity.Estudante;
import com.portalestagios.repository.EstudanteRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/estudantes")
@RequiredArgsConstructor
public class EstudanteController {

    private final EstudanteRepository estudanteRepository;

    @PostMapping
    public ResponseEntity<Estudante> create(@Valid @RequestBody Estudante estudante) {
        Estudante saved = estudanteRepository.save(estudante);
        return ResponseEntity.created(URI.create("/api/estudantes/" + saved.getId())).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estudante> findById(@PathVariable Long id) {
        return estudanteRepository.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ESTUDANTE') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Estudante> update(@PathVariable Long id, @Valid @RequestBody Estudante dto) {
        return estudanteRepository.findById(id)
                .map(e -> {
                    e.setNome(dto.getNome());
                    e.setCpf(dto.getCpf());
                    e.setCurso(dto.getCurso());
                    e.setEmail(dto.getEmail());
                    e.setTelefone(dto.getTelefone());
                    e.setAreasInteresse(dto.getAreasInteresse());
                    return ResponseEntity.ok(estudanteRepository.save(e));
                }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Estudante> list() {
        return estudanteRepository.findAll();
    }
}