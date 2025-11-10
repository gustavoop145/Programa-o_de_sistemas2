// src/main/java/com/portalestagios/repository/VagaRepository.java
package com.portalestagios.repository;

import com.portalestagios.entity.Vaga;
import com.portalestagios.entity.enums.StatusVaga;
import com.portalestagios.repository.projection.VagasPorAreaProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VagaRepository extends JpaRepository<Vaga, Long>, JpaSpecificationExecutor<Vaga> {

    // já existiam
    List<Vaga> findByStatus(StatusVaga status);
    long countByStatus(StatusVaga status);
    long countByArea_Id(Long areaId);

    // paginação + filtros via Specification
    Page<Vaga> findAll(Specification<Vaga> spec, Pageable pageable);

    // recomendações: vagas ABERTAS por lista de áreas do estudante (com
