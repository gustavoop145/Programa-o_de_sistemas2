// src/main/java/com/portalestagios/repository/InscricaoRepository.java
package com.portalestagios.repository;

import com.portalestagios.entity.Inscricao;
import com.portalestagios.entity.Vaga;
import com.portalestagios.entity.Estudante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {
    List<Inscricao> findByEstudante_Id(Long estudanteId);
    List<Inscricao> findByVaga_Id(Long vagaId);
    boolean existsByEstudanteAndVaga(Estudante estudante, Vaga vaga);
}