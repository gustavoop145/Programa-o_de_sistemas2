// src/main/java/com/portalestagios/config/SecurityConfig.java
package com.portalestagios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

  private final JwtAuthFilter jwtAuthFilter;
  private final UserDetailsService userDetailsService;

  public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserDetailsService userDetailsService) {
    this.jwtAuthFilter = jwtAuthFilter;
    this.userDetailsService = userDetailsService;
  }

  private static final String[] SWAGGER_WHITELIST = {
      "/v3/api-docs/**",
      "/swagger-ui.html",
      "/swagger-ui/**",
      "/api-docs/**"
  };

  // Rotas públicas (inclui /auth/** e /api/auth/**)
  private static final String[] PUBLIC_ENDPOINTS = {
      "/auth/**",
      "/api/auth/**",
      "/h2-console/**",
      "/error",
      "/actuator/health"
  };

  @Bean
  public DaoAuthenticationProvider daoAuthenticationProvider(PasswordEncoder encoder) {
    DaoAuthenticationProvider p = new DaoAuthenticationProvider();
    p.setUserDetailsService(userDetailsService);
    p.setPasswordEncoder(encoder);
    return p;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // API stateless com JWT
        .csrf(AbstractHttpConfigurer::disable)
        .headers(h -> h.frameOptions(f -> f.disable())) // para H2 console

        .cors(Customizer.withDefaults())
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        .authorizeHttpRequests(auth -> auth
            // Preflight CORS
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            // Públicos
            .requestMatchers(SWAGGER_WHITELIST).permitAll()
            .requestMatchers(PUBLIC_ENDPOINTS).permitAll()

            // Rota de diagnóstico do usuário logado
            .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()

            // Vagas
            .requestMatchers(HttpMethod.GET,  "/api/vagas/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/vagas/**").hasAnyRole("EMPRESA","ADMIN")
            .requestMatchers(HttpMethod.PUT,  "/api/vagas/**").hasAnyRole("EMPRESA","ADMIN")
            // Encerrar vaga: PATCH e pattern com subcaminho
            .requestMatchers(HttpMethod.PATCH, "/api/vagas/**/encerrar").hasAnyRole("EMPRESA","ADMIN")

            // Áreas de interesse (apenas ADMIN)
            .requestMatchers("/api/areas/**").hasRole("ADMIN")

            // Estudantes
            .requestMatchers(HttpMethod.POST, "/api/estudantes").permitAll()

            // Empresas
            .requestMatchers("/api/empresas/me/**").hasRole("EMPRESA")
            .requestMatchers("/api/empresas/**").hasRole("ADMIN")

            // Admin
            .requestMatchers("/api/admin/**").hasRole("ADMIN")

            // Qualquer outra rota exige autenticação
            .anyRequest().authenticated()
        )

        .authenticationProvider(daoAuthenticationProvider(passwordEncoder()))
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  // CORS amplo para desenvolvimento (ajuste para produção)
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration c = new CorsConfiguration();
    c.setAllowedOriginPatterns(java.util.List.of("*"));
    c.setAllowCredentials(false);
    c.setAllowedMethods(java.util.List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
    c.setAllowedHeaders(java.util.List.of("*"));
    c.setExposedHeaders(java.util.List.of("Authorization"));
    c.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
    src.registerCorsConfiguration("/**", c);
    return src;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
    return cfg.getAuthenticationManager();
  }
}