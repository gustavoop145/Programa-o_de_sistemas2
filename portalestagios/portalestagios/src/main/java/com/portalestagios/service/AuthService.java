package com.portalestagios.service;

import com.portalestagios.dto.AuthDTOs.*;
import com.portalestagios.entity.Usuario;
import com.portalestagios.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
public class AuthService {

  private final UsuarioRepository repo;
  private final PasswordEncoder encoder;
  private final AuthenticationManager authManager;
  private final JwtService jwt;

  public AuthService(UsuarioRepository repo, PasswordEncoder encoder,
                     AuthenticationManager authManager, JwtService jwt) {
    this.repo = repo;
    this.encoder = encoder;
    this.authManager = authManager;
    this.jwt = jwt;
  }

  @Transactional
  public void signup(SignupRequest dto) {
    String email = dto.email().trim().toLowerCase();
    if (repo.existsByEmailIgnoreCase(email)) {
      throw new IllegalArgumentException("E-mail já cadastrado");
    }
    Usuario u = Usuario.builder()
        .nome(dto.nome().trim())
        .email(email)
        .senhaHash(encoder.encode(dto.senha()))
        .roles(Set.of("ROLE_USER"))
        .build();
    repo.save(u);
  }

  public TokenResponse login(LoginRequest dto) {
    String email = dto.email().trim().toLowerCase();

    // dispara 401 se credenciais inválidas
    var token = new UsernamePasswordAuthenticationToken(email, dto.senha());
    authManager.authenticate(token);

    var user = repo.findByEmailIgnoreCase(email).orElseThrow();
    String jwtToken = jwt.generate(
        user.getEmail(),                      // subject
        Map.of("roles", user.getRoles())      // claims
    );
    return new TokenResponse(jwtToken, "Bearer");
  }
}