package com.portalestagios.controller;

import com.portalestagios.entity.AreaInteresse;
import com.portalestagios.repository.AreaInteresseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/areas")
public class AreaInteresseController {

  private final AreaInteresseRepository repo;

  public AreaInteresseController(AreaInteresseRepository repo) {
    this.repo = repo;
  }

  @GetMapping
  public List<AreaInteresse> listar() {
    return repo.findAll();
  }

  @PostMapping
  public ResponseEntity<?> criar(@RequestBody AreaInteresse body) {
    if (body.getNome() == null || body.getNome().isBlank()) {
      return ResponseEntity.badRequest().body("nome é obrigatório");
    }
    if (repo.existsByNomeIgnoreCase(body.getNome())) {
      return ResponseEntity.badRequest().body("Área já cadastrada");
    }
    AreaInteresse salvo = repo.save(AreaInteresse.builder().nome(body.getNome().trim()).build());
    return ResponseEntity.created(URI.create("/api/areas/" + salvo.getId())).body(salvo);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> buscar(@PathVariable Long id) {
    return repo.findById(id).<ResponseEntity<?>>map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody AreaInteresse body) {
    return repo.findById(id).map(ent -> {
      if (body.getNome() == null || body.getNome().isBlank())
        return ResponseEntity.badRequest().body("nome é obrigatório");
      ent.setNome(body.getNome().trim());
      return ResponseEntity.ok(repo.save(ent));
    }).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletar(@PathVariable Long id) {
    if (!repo.existsById(id)) return ResponseEntity.notFound().build();
    repo.deleteById(id);
    return ResponseEntity.noContent().build();
  }
}
