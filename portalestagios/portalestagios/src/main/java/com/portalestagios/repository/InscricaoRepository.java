package com.portalestagios.repository;

import com.portalestagios.entity.Estudante;
import com.portalestagios.entity.Inscricao;
import com.portalestagios.entity.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {

    // Verifica existência usando as entidades
    boolean existsByEstudanteAndVaga(Estudante estudante, Vaga vaga);

    // Alternativa: verifica existência por ids (caso outras partes do código usem ids)
    boolean existsByVagaIdAndEstudanteId(Long vagaId, Long estudanteId);

    // Busca por vaga (retorna lista de inscrições de uma vaga)
    List<Inscricao> findByVaga_Id(Long vagaId);

    // Busca por estudante (retorna lista de inscrições de um estudante)
    List<Inscricao> findByEstudante_Id(Long estudanteId);

    // -----------------------
    // Assinaturas para buscas por empresa (cobrem diferentes convenções de nome)
    // -----------------------

    // Convenção mais explícita: busca inscrições cujas vagas pertencem à empresa com esse id
    List<Inscricao> findByVaga_Empresa_Id(Long empresaId);

    // Alternativa com nome curto (muitas vezes usado por quem escreveu o controller)
    List<Inscricao> findByEmpresaId(Long empresaId);

    // Outra alternativa (caso a entidade Vaga tenha um campo empresaId direto)
    List<Inscricao> findByVagaEmpresaId(Long empresaId);
}