// src/main/java/com/portalestagios/config/SecurityConfig.java
package com.portalestagios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

  private final JwtAuthFilter jwtAuthFilter;

  public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
    this.jwtAuthFilter = jwtAuthFilter; // MESMO PACOTE (config) → não precisa importar de "security"
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth -> auth
        // públicos (swagger + auth)
        .requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**").permitAll()

        // vagas
        .requestMatchers(HttpMethod.GET, "/api/vagas/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/vagas/**").hasAnyRole("EMPRESA","ADMIN")
        .requestMatchers(HttpMethod.PUT,  "/api/vagas/**").hasAnyRole("EMPRESA","ADMIN")
        .requestMatchers(HttpMethod.POST, "/api/vagas/*/encerrar").hasAnyRole("EMPRESA","ADMIN")

        // estudantes
        .requestMatchers(HttpMethod.POST, "/api/estudantes").permitAll()

        // empresas (ajuste se quiser auto-cadastro)
        .requestMatchers(HttpMethod.POST, "/api/empresas").hasRole("ADMIN")
        .requestMatchers("/api/empresas/**").hasRole("ADMIN")

        // admin
        .requestMatchers("/api/admin/**").hasRole("ADMIN")

        // resto autenticado
        .anyRequest().authenticated()
      )
      .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
    return configuration.getAuthenticationManager();
  }
}