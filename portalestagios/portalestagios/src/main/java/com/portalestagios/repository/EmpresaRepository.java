package com.portalestagios.repository;

import com.portalestagios.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    // usado pelo /api/users/me e também em fluxos de autenticação
    Optional<Empresa> findByEmailIgnoreCase(String email);

    // buscas auxiliares comuns
    List<Empresa> findByNomeContainingIgnoreCase(String nome);

    // se sua entidade tiver estes campos (costuma ter):
    boolean existsByEmail(String email);
    boolean existsByCnpj(String cnpj);
}