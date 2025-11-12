package com.portalestagios.repository;

import com.portalestagios.entity.Vaga;
import com.portalestagios.entity.enums.StatusVaga;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VagaRepository
        extends JpaRepository<Vaga, Long>, JpaSpecificationExecutor<Vaga> {

    long countByStatus(StatusVaga status);

    Page<Vaga> findByStatus(StatusVaga status, Pageable pageable);

    Page<Vaga> findByStatusAndArea_IdIn(StatusVaga status, List<Long> areaIds, Pageable pageable);

    interface VagasPorAreaView {
        String getArea();
        long getQtd();
    }

    @Query("""
           select v.area.nome as area, count(v) as qtd
             from Vaga v
            group by v.area.nome
            order by qtd desc
           """)
    List<VagasPorAreaView> countVagasPorArea();
}
