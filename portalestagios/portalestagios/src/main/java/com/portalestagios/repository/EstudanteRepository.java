// src/main/java/com/portalestagios/repository/EstudanteRepository.java
package com.portalestagios.repository;

import com.portalestagios.entity.Estudante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstudanteRepository extends JpaRepository<Estudante, Long> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
}