// src/main/java/com/portalestagios/config/SecurityConfig.java
package com.portalestagios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity(prePostEnabled = true) // habilita @PreAuthorize nos controllers
public class SecurityConfig {

  private final JwtAuthFilter jwtAuthFilter;

  public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
    this.jwtAuthFilter = jwtAuthFilter; // mesmo pacote (config)
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .cors(Customizer.withDefaults()) // habilita o CORS com o bean abaixo
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth -> auth
        // Preflight CORS
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

        // Públicos (auth + swagger + error)
        .requestMatchers("/auth/**").permitAll()
        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/api-docs/**").permitAll()
        .requestMatchers("/error").permitAll()

        // Vagas
        .requestMatchers(HttpMethod.GET, "/api/vagas/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/vagas/**").hasAnyRole("EMPRESA","ADMIN")
        .requestMatchers(HttpMethod.PUT,  "/api/vagas/**").hasAnyRole("EMPRESA","ADMIN")
        .requestMatchers(HttpMethod.POST, "/api/vagas/*/encerrar").hasAnyRole("EMPRESA","ADMIN")

        // Áreas de interesse (somente ADMIN)
        .requestMatchers("/api/areas/**").hasRole("ADMIN")

        // Estudantes
        .requestMatchers(HttpMethod.POST, "/api/estudantes").permitAll() // auto-cadastro (opcional)
        // Demais endpoints de estudante controlados via @PreAuthorize

        // Empresas
        .requestMatchers("/api/empresas/me/**").hasRole("EMPRESA") // painel da própria empresa
        .requestMatchers("/api/empresas/**").hasRole("ADMIN")      // manutenção de empresas pelo admin

        // Admin
        .requestMatchers("/api/admin/**").hasRole("ADMIN")

        // Qualquer outra rota precisa estar autenticada
        .anyRequest().authenticated()
      )
      .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  // Bean CORS: ajuste as origens conforme sua SPA (dev/prod)
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration c = new CorsConfiguration();

    // Em desenvolvimento (Vite)
    c.setAllowedOrigins(java.util.List.of(
      "http://localhost:5173",
      "https://SEU-DOMINIO-NA-VERCEL.app" // TODO: troque para o domínio real de produção
    ));
    // Se precisar wildcard:
    // c.setAllowedOriginPatterns(java.util.List.of("https://*.seu-dominio.com"));

    c.setAllowedMethods(java.util.List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
    c.setAllowedHeaders(java.util.List.of("*"));
    c.setExposedHeaders(java.util.List.of("Authorization")); // SPA pode ler o header Authorization, se necessário
    c.setAllowCredentials(true); // se for usar cookies/withCredentials
    c.setMaxAge(3600L); // cache do preflight (1h)

    UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
    src.registerCorsConfiguration("/**", c);
    return src;
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