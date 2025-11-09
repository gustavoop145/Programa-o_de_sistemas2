// src/main/java/com/portalestagios/config/JwtAuthFilter.java
package com.portalestagios.config;

import com.portalestagios.repository.UsuarioRepository;
import com.portalestagios.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UsuarioRepository usuarioRepository;

  private static final AntPathMatcher MATCHER = new AntPathMatcher();
  private static final List<String> PUBLIC_PATHS = List.of(
      "/auth/**",
      "/swagger-ui.html",
      "/swagger-ui/**",
      "/v3/api-docs/**",
      "/api-docs/**"
  );

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain chain
  ) throws ServletException, IOException {

    // 1) Preflight CORS: deixa passar
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      chain.doFilter(request, response);
      return;
    }

    // 2) Rotas públicas: deixa passar
    String path = request.getServletPath();
    for (String pattern : PUBLIC_PATHS) {
      if (MATCHER.match(pattern, path)) {
        chain.doFilter(request, response);
        return;
      }
    }

    // 3) Se já existe autenticação no contexto, segue
    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      chain.doFilter(request, response);
      return;
    }

    // 4) Extrai token do header
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
      chain.doFilter(request, response);
      return;
    }
    String token = header.substring(7).trim();

    try {
      // 5) Pega o subject (email) do JWT
      String email = jwtService.getSubject(token);
      if (!StringUtils.hasText(email)) {
        chain.doFilter(request, response);
        return;
      }

      // (Opcional) validar expiração/assinatura caso seu JwtService possua método específico
      // if (!jwtService.isValid(token, email)) { chain.doFilter(request, response); return; }

      // 6) Busca usuário e monta authorities a partir das roles persistidas
      var optUser = usuarioRepository.findByEmailIgnoreCase(email);
      if (optUser.isEmpty()) {
        chain.doFilter(request, response);
        return;
      }

      var user = optUser.get();
      var authorities = user.getRoles().stream()
          .map(SimpleGrantedAuthority::new)     // "ROLE_EMPRESA", "ROLE_ADMIN", etc.
          .toList();

      // 7) Preenche o SecurityContext
      var authToken = new UsernamePasswordAuthenticationToken(
          user,        // principal (pode ser o próprio objeto Usuario)
          null,        // credentials
          authorities  // granted authorities
      );
      authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authToken);

    } catch (Exception ignored) {
      // token inválido/expirado: segue sem autenticação
    }

    chain.doFilter(request, response);
  }
}