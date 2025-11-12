package com.portalestagios.repository;

import com.portalestagios.entity.Estudante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstudanteRepository extends JpaRepository<Estudante, Long> {

    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);

    Optional<Estudante> findByEmailIgnoreCase(String email);
    Optional<Estudante> findByEmail(String email);
}