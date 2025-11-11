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

    // Básicos
    List<Vaga> findByStatus(StatusVaga status);
    long countByStatus(StatusVaga status);
    long countByArea_Id(Long areaId);

    // Paginação + filtros via Specification
    Page<Vaga> findAll(Specification<Vaga> spec, Pageable pageable);

    // Recomendação por áreas do estudante (ABERTAS) — sem 'createdAt'
    Page<Vaga> findByStatusAndArea_IdIn(StatusVaga status, List<Long> areaIds, Pageable pageable);

    // Mesma consulta com ordenação segura por ID desc (se quiser ordem decrescente recente)
    Page<Vaga> findByStatusAndArea_IdInOrderByIdDesc(StatusVaga status, List<Long> areaIds, Pageable pageable);

    // Dashboard: quantidade de vagas por área
    @Query("""
           select v.area.nome as area, count(v) as total
           from Vaga v
           group by v.area.nome
           """)
    List<VagasPorAreaProjection> countVagasPorArea();
}
