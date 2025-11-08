package com.portalestagios.service;

import com.portalestagios.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioDetailsService implements UserDetailsService {

  private final UsuarioRepository repo;

  public UsuarioDetailsService(UsuarioRepository repo) {
    this.repo = repo;
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    String email = username.trim().toLowerCase();

    var u = repo.findByEmailIgnoreCase(email)
        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

    var authorities = (u.getRoles() == null || u.getRoles().isEmpty())
        ? List.of(new SimpleGrantedAuthority("ROLE_USER"))
        : u.getRoles().stream().map(SimpleGrantedAuthority::new).toList();

    return new org.springframework.security.core.userdetails.User(
        u.getEmail(),
        u.getSenhaHash(),
        authorities
    );
  }
}