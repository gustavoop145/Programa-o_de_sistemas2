// src/main/java/com/portalestagios/repository/EmpresaRepository.java
package com.portalestagios.repository;

import com.portalestagios.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    boolean existsByCnpj(String cnpj);
    boolean existsByEmail(String email);
}