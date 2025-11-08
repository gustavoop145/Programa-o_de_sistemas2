package com.portalestagios.config;

import com.portalestagios.repository.UsuarioRepository;
import com.portalestagios.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UsuarioRepository usuarioRepository;

  public JwtAuthFilter(JwtService jwtService, UsuarioRepository usuarioRepository) {
    this.jwtService = jwtService;
    this.usuarioRepository = usuarioRepository;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
      String token = header.substring(7);
      try {
        String email = jwtService.getSubject(token);
        var optUser = usuarioRepository.findByEmailIgnoreCase(email);
        if (optUser.isPresent()) {
          var user = optUser.get();
          var authorities = user.getRoles().stream()
              .map(SimpleGrantedAuthority::new)
              .collect(Collectors.toList());
          var auth = new UsernamePasswordAuthenticationToken(email, null, authorities);
          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      } catch (Exception ignored) {
        // token inválido/expirado -> segue sem autenticação
      }
    }
    chain.doFilter(request, response);
  }
}