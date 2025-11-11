package com.portalestagios.config;

import com.portalestagios.entity.*;
import com.portalestagios.entity.enums.Modalidade;
import com.portalestagios.entity.enums.StatusVaga;
import com.portalestagios.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataSeeder {

  @Bean
  CommandLineRunner seed(UsuarioRepository usuarios, AreaInteresseRepository areas,
                         EmpresaRepository empresas, VagaRepository vagas,
                         PasswordEncoder encoder) {
    return (args) -> {
      if (areas.count() == 0) {
        areas.save(AreaInteresse.builder().nome("Desenvolvimento").build());
        areas.save(AreaInteresse.builder().nome("Dados/BI").build());
        areas.save(AreaInteresse.builder().nome("UX/UI").build());
      }

      usuarios.findByEmailIgnoreCase("admin@portal.com").orElseGet(() ->
          usuarios.save(Usuario.builder()
              .nome("Administrador") // <-- necessário (NOT NULL)
              .email("admin@portal.com")
              .senhaHash(encoder.encode("admin123"))
              .roles(Set.of("ROLE_ADMIN"))
              .build())
      );

      var dev = areas.findByNomeIgnoreCase("Desenvolvimento").orElseThrow();

      var emp = empresas.findByEmailIgnoreCase("empresa@acme.com").orElseGet(() ->
          empresas.save(Empresa.builder()
              .nome("ACME Tech")
              .email("empresa@acme.com")
              .cnpj("00.000.000/0001-00")
              .telefone("11999999999")
              .endereco("Av. Paulista, 1000, São Paulo - SP")
              .areasAtuacao("Tecnologia")
              .build())
      );

      if (vagas.count() == 0) {
        vagas.save(Vaga.builder()
            .titulo("Estágio em Java")
            .descricao("Suporte no desenvolvimento de APIs Spring.")
            .area(dev)
            .localizacao("São Paulo - SP")
            .modalidade(Modalidade.HIBRIDO)
            .cargaHoraria(30)
            .requisitos("Java, Git, SQL")
            .status(StatusVaga.ABERTA)
            .empresa(emp)
            .build());
      }
    };
  }
}
