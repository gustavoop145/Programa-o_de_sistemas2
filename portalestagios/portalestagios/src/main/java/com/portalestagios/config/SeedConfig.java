package com.portalestagios.config;

import com.portalestagios.entity.Usuario;
import com.portalestagios.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.LinkedHashSet;
import java.util.Set;

@Configuration
public class SeedConfig {

  @Bean
  CommandLineRunner seedAdmin(UsuarioRepository repo, PasswordEncoder encoder) {
    return args -> {
      final String email = "admin@teste.com";

      // >>> usa o método que você TEM no repository
      Usuario u = repo.findByEmailIgnoreCase(email).orElseGet(Usuario::new);

      u.setNome("Admin");
      u.setEmail(email);

      // salva SEMPRE o hash BCrypt no campo 'senhaHash'
      u.setSenhaHash(encoder.encode("123456"));

      // como você usa hasRole("ADMIN"), as authorities precisam vir como "ROLE_ADMIN"
      Set<String> roles = new LinkedHashSet<>();
      roles.add("ROLE_ADMIN");
      roles.add("ROLE_USER"); // opcional
      u.setRoles(roles);

      repo.save(u);
      System.out.println(">>> Seed OK: admin@teste.com / 123456 (ROLE_ADMIN)");
    };
  }
}