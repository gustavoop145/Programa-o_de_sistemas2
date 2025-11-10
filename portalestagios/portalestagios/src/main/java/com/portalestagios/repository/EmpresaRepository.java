package com.portalestagios.repository;

import com.portalestagios.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    // usado no login e painel da empresa
    Optional<Empresa> findByEmailIgnoreCase(String email);

    // opcional (busca parcial por nome)
    List<Empresa> findByNomeContainingIgnoreCase(String nome);
}