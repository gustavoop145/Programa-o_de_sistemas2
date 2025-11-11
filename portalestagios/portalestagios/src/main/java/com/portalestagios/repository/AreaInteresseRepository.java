package com.portalestagios.repository;

import com.portalestagios.entity.AreaInteresse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AreaInteresseRepository extends JpaRepository<AreaInteresse, Long> {

    // verifica se o nome já existe (cadastro)
    boolean existsByNomeIgnoreCase(String nome);

    // busca por nome (seeder)
    Optional<AreaInteresse> findByNomeIgnoreCase(String nome);

    // áreas de interesse vinculadas a um estudante específico
    // OBS: usa o nome correto do campo em Estudante: areasInteresse
    @Query("select a from Estudante e join e.areasInteresse a where e.id = :estudanteId")
    List<AreaInteresse> findAllByEstudanteId(@Param("estudanteId") Long estudanteId);
}