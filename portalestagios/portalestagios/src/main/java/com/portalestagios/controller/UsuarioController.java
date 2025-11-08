package com.portalestagios.controller;

import com.portalestagios.dto.UsuarioAreasUpdateDTO;
import com.portalestagios.dto.UsuarioCreateDTO;
import com.portalestagios.dto.UsuarioResponseDTO;
import com.portalestagios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

  private final UsuarioService service;

  public UsuarioController(UsuarioService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<UsuarioResponseDTO> criar(@Valid @RequestBody UsuarioCreateDTO body) {
    var salvo = service.criar(body);
    return ResponseEntity.created(URI.create("/api/usuarios/" + salvo.id())).body(salvo);
  }

  @GetMapping
  public List<UsuarioResponseDTO> listar() {
    return service.listar();
  }

  @PutMapping("/{id}/areas")
  public UsuarioResponseDTO definirAreas(@PathVariable Long id, @Valid @RequestBody UsuarioAreasUpdateDTO body) {
    return service.definirAreas(id, body.idsAreas());
  }

  @GetMapping("/{id}/areas")
  public List<UsuarioResponseDTO.AreaDTO> listarAreas(@PathVariable Long id) {
    return service.listarAreasDoUsuario(id);
  }
}