// src/main/java/com/portalestagios/repository/InscricaoRepository.java
package com.portalestagios.repository;

import com.portalestagios.entity.Inscricao;
import com.portalestagios.entity.Vaga;
import com.portalestagios.entity.Estudante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {

    // já existentes
    List<Inscricao> findByEstudante_Id(Long estudanteId);
    List<Inscricao> findByVaga_Id(Long vagaId);
    boolean existsByEstudanteAndVaga(Estudante estudante, Vaga vaga);

    // ===== Painel da empresa =====
    // Todas as inscrições em vagas pertencentes a uma empresa
    @Query("select i from Inscricao i where i.vaga.empresa.id = :empresaId")
    List<Inscricao> findByEmpresaId(Long empresaId);

    // (Opcional) mesma consulta com paginação
    @Query("select i from Inscricao i where i.vaga.empresa.id = :empresaId")
    Page<Inscricao> findByEmpresaId(Long empresaId, Pageable pageable);

    // ===== Extras úteis (opcionais) =====
    long countByVaga_Id(Long vagaId);
    long countByEstudante_Id(Long estudanteId);
}