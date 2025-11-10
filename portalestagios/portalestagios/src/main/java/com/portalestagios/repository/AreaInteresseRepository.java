package com.portalestagios.repository;

import com.portalestagios.entity.AreaInteresse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AreaInteresseRepository extends JpaRepository<AreaInteresse, Long> {

    // já existente — verifica se o nome da área já existe (usado no cadastro)
    boolean existsByNomeIgnoreCase(String nome);

    // busca uma área pelo nome (usado no DataSeeder)
    Optional<AreaInteresse> findByNomeIgnoreCase(String nome);

    // busca todas as áreas associadas a um estudante (usado em vagas recomendadas)
    List<AreaInteresse> findAllByEstudantes_Id(Long estudanteId);
}
