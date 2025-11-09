// src/main/java/com/portalestagios/controller/EmpresaController.java
package com.portalestagios.controller;

import com.portalestagios.entity.Empresa;
import com.portalestagios.repository.EmpresaRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaRepository empresaRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Empresa> create(@Valid @RequestBody Empresa empresa) {
        Empresa saved = empresaRepository.save(empresa);
        return ResponseEntity.created(URI.create("/api/empresas/" + saved.getId())).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empresa> findById(@PathVariable Long id) {
        return empresaRepository.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Empresa> update(@PathVariable Long id, @Valid @RequestBody Empresa dto) {
        return empresaRepository.findById(id)
                .map(e -> {
                    e.setNome(dto.getNome());
                    e.setCnpj(dto.getCnpj());
                    e.setEmail(dto.getEmail());
                    e.setTelefone(dto.getTelefone());
                    e.setEndereco(dto.getEndereco());
                    e.setAreasAtuacao(dto.getAreasAtuacao());
                    return ResponseEntity.ok(empresaRepository.save(e));
                }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!empresaRepository.existsById(id)) return ResponseEntity.notFound().build();
        empresaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Empresa> list() {
        return empresaRepository.findAll();
    }
}