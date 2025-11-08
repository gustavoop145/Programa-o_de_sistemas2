package com.portalestagios.service;

import com.portalestagios.dto.UsuarioCreateDTO;
import com.portalestagios.dto.UsuarioResponseDTO;
import com.portalestagios.entity.AreaInteresse;
import com.portalestagios.entity.Usuario;
import com.portalestagios.repository.AreaInteresseRepository;
import com.portalestagios.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

  private final UsuarioRepository repo;
  private final AreaInteresseRepository areaRepo;
  private final PasswordEncoder encoder;

  public UsuarioService(UsuarioRepository repo, AreaInteresseRepository areaRepo, PasswordEncoder encoder) {
    this.repo = repo;
    this.areaRepo = areaRepo;
    this.encoder = encoder;
  }

  @Transactional
  public UsuarioResponseDTO criar(UsuarioCreateDTO dto) {
    if (repo.existsByEmailIgnoreCase(dto.email())) {
      throw new IllegalArgumentException("E-mail já cadastrado");
    }
    Usuario u = Usuario.builder()
        .nome(dto.nome().trim())
        .email(dto.email().trim())
        .senhaHash(encoder.encode(dto.senha()))
        .build();
    u = repo.save(u);
    return toDTO(u);
  }

  @Transactional(readOnly = true)
  public List<UsuarioResponseDTO> listar() {
    return repo.findAll().stream().map(this::toDTO).toList();
  }

  @Transactional
  public UsuarioResponseDTO definirAreas(Long usuarioId, List<Long> idsAreas) {
    Usuario u = repo.findById(usuarioId)
        .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    var areas = areaRepo.findAllById(idsAreas);
    if (areas.size() != idsAreas.size()) {
      throw new IllegalArgumentException("Uma ou mais áreas não existem");
    }
    u.getAreas().clear();
    u.getAreas().addAll(areas);
    return toDTO(u); // JPA sincroniza no commit
  }

  @Transactional(readOnly = true)
  public List<UsuarioResponseDTO.AreaDTO> listarAreasDoUsuario(Long usuarioId) {
    Usuario u = repo.findById(usuarioId)
        .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    return u.getAreas().stream()
        .map(a -> new UsuarioResponseDTO.AreaDTO(a.getId(), a.getNome()))
        .toList();
  }

  private UsuarioResponseDTO toDTO(Usuario u) {
    var areas = u.getAreas().stream()
        .map(a -> new UsuarioResponseDTO.AreaDTO(a.getId(), a.getNome()))
        .toList();
    return new UsuarioResponseDTO(u.getId(), u.getNome(), u.getEmail(), areas);
  }
}