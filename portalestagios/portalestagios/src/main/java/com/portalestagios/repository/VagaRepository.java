// src/main/java/com/portalestagios/repository/VagaRepository.java
package com.portalestagios.repository;

import com.portalestagios.entity.Vaga;
import com.portalestagios.entity.enums.StatusVaga;
import com.portalestagios.repository.projection.VagasPorAreaProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VagaRepository extends JpaRepository<Vaga, Long> {
    List<Vaga> findByStatus(StatusVaga status);
    long countByStatus(StatusVaga status);
    long countByArea_Id(Long areaId);

    @Query("""
           select v.area.nome as area, count(v) as total
           from Vaga v
           group by v.area.nome
           """)
    List<VagasPorAreaProjection> countVagasPorArea();
}